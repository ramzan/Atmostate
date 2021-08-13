package ca.ramzan.atmostate.repository

import android.util.Log
import ca.ramzan.atmostate.database.cities.CityDatabaseDao
import ca.ramzan.atmostate.database.cities.DbSavedCity
import ca.ramzan.atmostate.database.cities.asDomainModel
import ca.ramzan.atmostate.database.weather.*
import ca.ramzan.atmostate.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class RefreshState {
    object Loading : RefreshState()
    object Loaded : RefreshState()
    data class Error(val error: String) : RefreshState()
}

class WeatherRepository(
    private val weatherDb: WeatherDatabaseDao,
    private val cityDb: CityDatabaseDao,
    private val api: WeatherApi
) {

    val currentForecast =
        weatherDb.getCurrentForecast().map {
            it?.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)
    val hourlyForecast =
        weatherDb.getHourlyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val dailyForecast =
        weatherDb.getDailyForecast().map {
            it.asDomainModel()
        }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val alerts = weatherDb.getAlerts().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())

    val allCities = cityDb.getAllCities().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val savedCities = cityDb.getSavedCities().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val currentCity =
        cityDb.getSelectedCityFlow().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)


    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loaded)
    val refreshState: StateFlow<RefreshState> get() = _refreshState

    init {
        CoroutineScope(Dispatchers.Default).launch {
            cityDb.getSavedCityIds().forEach { getWeather(it) }
        }
    }

    suspend fun refreshSelectedCity() {
        currentCity.value?.run {
            getWeather(id)
        }
    }

    private suspend fun getWeather(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            val lastRefresh = weatherDb.lastUpdated(cityId)
            if (lastRefresh != null && (System.currentTimeMillis() - lastRefresh * 1000 < 600000)) {
                Log.d("getWeather", "Skip refresh")
                return@launch
            }
            _refreshState.emit(RefreshState.Loading)
            Log.d("getWeather", "Refreshing")
            val (lat, lon) = cityDb.getCoordinates(cityId)
            api.getForecast(lat, lon).run {
                when (this) {
                    is WeatherResult.Failure -> {
                        Log.d("getWeather", "Fail")
                        Log.d("getWeather", error)
                        _refreshState.emit(RefreshState.Error(this.error))
                    }
                    is WeatherResult.Success -> {
                        Log.d("getWeather", "Success")
                        this.run {
                            weatherDb.saveForecast(
                                cityId,
                                current.asDatabaseModel(cityId, timezone),
                                hourly.asDatabaseModel(cityId, timezone),
                                daily.asDatabaseModel(cityId, timezone),
                                alerts.asDatabaseModel(cityId, timezone)
                            )
                            _refreshState.emit(RefreshState.Loaded)
                        }
                    }
                }
            }
        }
    }

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(DbSavedCity(id))
            getWeather(id)
        }
    }

    fun setCurrentCity(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(cityDb.getCity(cityId))
            getWeather(cityId)
        }
    }
}
