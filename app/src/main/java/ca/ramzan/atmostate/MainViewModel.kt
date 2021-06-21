package ca.ramzan.atmostate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.network.WeatherRepository
import ca.ramzan.atmostate.network.WeatherResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MainState() {
    object Loading : MainState()
    data class Loaded(
        val data: WeatherResult.Success,
    ) : MainState()

    data class Error(val error: String) : MainState()
}

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> get() = _state

    init {
        val lat = 53.44
        val lon = -94.04
        viewModelScope.launch {
            WeatherRepository.getForecast(lat, lon).run {
                when (this) {
                    is WeatherResult.Failure -> _state.emit(MainState.Error(this.error))
                    is WeatherResult.Success -> _state.emit(MainState.Loaded(this))
                }
            }

        }
    }
}