package ca.ramzan.atmostate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.network.WeatherResult
import ca.ramzan.atmostate.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class MainState {
    object Loading : MainState()
    data class Loaded(
        val data: WeatherResult.Success,
    ) : MainState()

    data class Error(val error: String) : MainState()
}

@HiltViewModel
class MainViewModel @Inject constructor(repo: WeatherRepository) : ViewModel() {

    private val _state = MutableStateFlow<MainState>(MainState.Loading)
    val state: StateFlow<MainState> get() = _state

    init {
        viewModelScope.launch {
            repo.weather.collect {
                _state.emit(it)
            }
        }
    }
}
