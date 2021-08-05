package ca.ramzan.atmostate.repository

import ca.ramzan.atmostate.MainState
import ca.ramzan.atmostate.database.WeatherDatabaseDao
import ca.ramzan.atmostate.network.WeatherApi
import ca.ramzan.atmostate.network.WeatherResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherRepository(private val db: WeatherDatabaseDao, private val api: WeatherApi) {

    private val lat = 43.5789
    private val lon = -79.6583

    private val _weather = MutableStateFlow<MainState>(MainState.Loading)
    val weather: StateFlow<MainState> get() = _weather

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getWeather()
        }
    }

    private suspend fun getWeather() {
        api.getForecast(lat, lon).run {
            _weather.emit(
                when (this) {
                    is WeatherResult.Failure -> MainState.Error(this.error)
                    is WeatherResult.Success -> MainState.Loaded(this)
                }
            )
        }
    }
}