package ca.ramzan.atmostate.repository

import android.util.Log
import ca.ramzan.atmostate.database.cities.CityDatabaseDao
import ca.ramzan.atmostate.database.cities.CityDisplay
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

    private val lat = 48.5789
    private val lon = -79.6583

    val currentForecast =
        weatherDb.getCurrentForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, null)
    val hourlyForecast =
        weatherDb.getHourlyForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val dailyForecast =
        weatherDb.getDailyForecast().stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())
    val alerts = weatherDb.getAlerts()

    val allCities = cityDb.getAllCities().map { cityNames ->
        cityNames.map { c ->
            CityDisplay(c.id, "${c.city}${c.state?.let { ", $it" }}${c.country?.let { ", $it" }}")
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())

    val savedCities = cityDb.getSavedCities().map { cityNames ->
        cityNames.map { c ->
            CityDisplay(c.id, "${c.city}${c.state?.let { ", $it" }}${c.country?.let { ", $it" }}")
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), Eagerly, emptyList())


    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loaded)
    val refreshState: StateFlow<RefreshState> get() = _refreshState

    init {
        CoroutineScope(Dispatchers.Default).launch {
            getWeather()
        }
    }

    suspend fun getWeather() {
        val lastRefresh = currentForecast.value?.date
        if (lastRefresh != null && (System.currentTimeMillis() - lastRefresh * 1000 < 600000)) {
            Log.d("getWeather", "Skip refresh")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            _refreshState.emit(RefreshState.Loading)
            Log.d("getWeather", "Refreshing")
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
                            weatherDb.clearOldForecast()
                            saveCurrentForecast(current)
                            saveHourlyForecast(hourly)
                            saveDailyForecast(daily)
                            saveAlerts(alerts)
                            _refreshState.emit(RefreshState.Loaded)
                        }
                    }
                }
            }
        }
    }

    private suspend fun saveCurrentForecast(current: Current) {
        current.run {
            weatherDb.insertCurrent(
                DbCurrent(
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
            )
        }
    }

    private suspend fun saveHourlyForecast(hourlies: List<Hourly>) {
        hourlies.map { hourly ->
            hourly.run {
                DbHourly(
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
        }.also {
            weatherDb.insertHourly(it)
        }
    }

    private suspend fun saveDailyForecast(dailies: List<Daily>) {
        dailies.map { daily ->
            daily.run {
                DbDaily(
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
        }.also {
            weatherDb.insertDaily(it)
        }
    }

    private suspend fun saveAlerts(alerts: List<Alert>?) {
        if (alerts == null) return
        alerts.map { alert ->
            alert.run {
                DbAlert(
                    senderName = senderName,
                    event = event,
                    start = start,
                    end = end,
                    description = description,
                )
            }
        }.also {
            weatherDb.insertAlerts(it)
        }
    }

    fun addCity(id: Long) {
        CoroutineScope(Dispatchers.IO).launch {
            cityDb.saveCity(SavedCity(id))
        }
    }
}