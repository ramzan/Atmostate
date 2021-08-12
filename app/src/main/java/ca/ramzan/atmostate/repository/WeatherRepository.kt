package ca.ramzan.atmostate.repository

import android.util.Log
import ca.ramzan.atmostate.database.cities.CityDatabaseDao
import ca.ramzan.atmostate.database.cities.CityDisplay
import ca.ramzan.atmostate.database.cities.CityName
import ca.ramzan.atmostate.database.cities.SavedCity
import ca.ramzan.atmostate.database.weather.*
import ca.ramzan.atmostate.network.*
import ca.ramzan.atmostate.ui.forecast.capitalized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

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
        weatherDb.getCurrentForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)
    val hourlyForecast =
        weatherDb.getHourlyForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val dailyForecast =
        weatherDb.getDailyForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val alerts = weatherDb.getAlerts()

    val allCities = cityDb.getAllCities().map { cityNames ->
        cityNames.map { it.toCityDisplay() }
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())

    val savedCities = cityDb.getSavedCities().map { cityNames ->
        cityNames.map { it.toCityDisplay() }
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
                                currentToEntity(cityId, current),
                                hourlyToEntity(cityId, hourly),
                                dailyToEntity(cityId, daily),
                                alertsToEntity(cityId, alerts)
                            )
                            _refreshState.emit(RefreshState.Loaded)
                        }
                    }
                }
            }
        }
    }

    private fun currentToEntity(cityId: Long, current: Current): DbCurrent {
        return current.run {
            DbCurrent(
                cityId = cityId,
                date = dt,
                sunrise = sunrise,
                sunset = sunset,
                temp = temp.roundToInt(),
                feelsLike = feelsLike.roundToInt(),
                pressure = pressure / 10,
                humidity = humidity.roundToInt(),
                dewPoint = dewPoint.roundToInt(),
                clouds = clouds.roundToInt(),
                uvi = uvi.roundToInt(),
                visibility = (visibility / 1000).roundToInt(),
                windSpeed = (windSpeed * 3.6).roundToInt(),
                windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                windDeg = windDeg,
                icon = weather.first().icon,
                description = weather.first().description.capitalized(),
            )
        }
    }

    private fun hourlyToEntity(cityId: Long, hourlies: List<Hourly>): List<DbHourly> {
        return hourlies.map { hourly ->
            hourly.run {
                DbHourly(
                    cityId = cityId,
                    date = dt,
                    temp = temp.roundToInt(),
                    feelsLike = feelsLike.roundToInt(),
                    windSpeed = (windSpeed * 3.6).roundToInt(),
                    windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                    windDeg = windDeg,
                    pop = (pop * 100).roundToInt(),
                    rain = rain?.hour,
                    snow = snow?.hour,
                    icon = weather.first().icon,
                    description = weather.first().description.capitalized(),
                )
            }
        }
    }

    private fun dailyToEntity(cityId: Long, dailies: List<Daily>): List<DbDaily> {
        return dailies.map { daily ->
            daily.run {
                DbDaily(
                    cityId = cityId,
                    date = dt,
                    tempMin = temp.min.roundToInt(),
                    tempMax = temp.max.roundToInt(),
                    tempMorn = temp.morn.roundToInt(),
                    tempDay = temp.day.roundToInt(),
                    tempEve = temp.eve.roundToInt(),
                    tempNight = temp.night.roundToInt(),
                    feelsLikeMorn = feelsLike?.morn?.roundToInt(),
                    feelsLikeDay = feelsLike?.day?.roundToInt(),
                    feelsLikeEve = feelsLike?.eve?.roundToInt(),
                    feelsLikeNight = feelsLike?.night?.roundToInt(),
                    humidity = humidity.roundToInt(),
                    windSpeed = (windSpeed * 3.6).roundToInt(),
                    windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                    windDeg = windDeg,
                    pop = (pop * 100).roundToInt(),
                    rain = rain,
                    snow = snow,
                    icon = weather.first().icon,
                    description = weather.first().description.capitalized(),
                )
            }
        }
    }

    private fun alertsToEntity(cityId: Long, alerts: List<Alert>?): List<DbAlert> {
        return alerts?.mapIndexed { i, alert ->
            alert.run {
                DbAlert(
                    cityId = cityId,
                    alertId = i.toLong(),
                    senderName = senderName,
                    event = event,
                    start = start,
                    end = end,
                    description = description,
                )
            }
        } ?: emptyList()
    }

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(SavedCity(id))
            getWeather(id)
        }
    }

    fun setCurrentCity(cityId: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.selectCity(cityDb.getCity(cityId))
        }
    }
}

private fun CityName.toCityDisplay(): CityDisplay {
    return CityDisplay(
        id,
        "${city}${state?.let { ", $it" } ?: ""}${country?.let { ", $it" } ?: ""}")
}
