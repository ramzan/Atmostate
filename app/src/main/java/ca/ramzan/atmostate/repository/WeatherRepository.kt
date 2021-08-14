package ca.ramzan.atmostate.repository

import android.util.Log
import ca.ramzan.atmostate.database.cities.CityDatabaseDao
import ca.ramzan.atmostate.database.cities.DbSavedCity
import ca.ramzan.atmostate.database.cities.asDomainModel
import ca.ramzan.atmostate.database.weather.*
import ca.ramzan.atmostate.network.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
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
    val savedCities = cityDb.getSavedCities().map {
        it.asDomainModel()
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val currentCity =
        cityDb.getSelectedCityFlow().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)

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

    fun setCurrentCity(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(cityDb.getCity(cityId))
            getWeather(cityId)
        }
    }

    //------------------------------------------------------------------------------------------

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(DbSavedCity(id))
            getWeather(id)
        }
    }

    fun removeCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.removeCity(id)
            weatherDb.clearForecast(id)
        }
    }

    //------------------------------------------------------------------------------------------

    private val countryId = MutableStateFlow(1L)

    fun setCurrentCountry(newCountryId: Long) {
        CoroutineScope(Dispatchers.Default).launch {
            countryId.emit(newCountryId)
        }
    }

    private val savedCityIds =
        cityDb.getSavedCityIdsFlow().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())

    val citiesForCountry = countryId.combine(savedCityIds) { countryId, savedCitiesIds ->
        cityDb.getCitiesForCountry(countryId).asDomainModel(savedCitiesIds)
    }

    suspend fun getAllCountries() = cityDb.getAllCountries()

    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loaded)
    val refreshState: StateFlow<RefreshState> get() = _refreshState
}
