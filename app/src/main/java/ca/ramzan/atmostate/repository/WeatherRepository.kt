package ca.ramzan.atmostate.repository

import ca.ramzan.atmostate.database.*
import ca.ramzan.atmostate.network.*
import ca.ramzan.atmostate.ui.capitalized
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

sealed class RefreshState {
    object Loading : RefreshState()
    object Loaded : RefreshState()
    data class Error(val error: String) : RefreshState()
}

class WeatherRepository(private val db: WeatherDatabaseDao, private val api: WeatherApi) {

    private val lat = 43.5789
    private val lon = -79.6583

    val currentForecast = db.getCurrentForecast()
    val hourlyForecast = db.getHourlyForecast()
    val dailyForecast = db.getDailyForecast()
    val alerts = db.getAlerts()

    private val _refreshState = MutableStateFlow<RefreshState>(RefreshState.Loading)
    val refreshState: StateFlow<RefreshState> get() = _refreshState

    init {
        CoroutineScope(Dispatchers.Default).launch {
            getWeather()
        }
    }

    suspend fun getWeather() {
        CoroutineScope(Dispatchers.IO).launch {
            _refreshState.emit(RefreshState.Loading)
            api.getForecast(lat, lon).run {
                when (this) {
                    is WeatherResult.Failure -> _refreshState.emit(RefreshState.Error(this.error))
                    is WeatherResult.Success -> {
                        this.run {
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
            db.insertCurrent(
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
            db.insertHourly(it)
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
            db.insertDaily(it)
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
            db.insertAlerts(it)
        }
    }
}