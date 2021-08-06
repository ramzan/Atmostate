package ca.ramzan.atmostate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {
    fun refresh() {
        viewModelScope.launch {
            repo.getWeather()
        }
    }

    val state = repo.refreshState

    val currentForecast = repo.currentForecast
    val hourlyForecast = repo.hourlyForecast
    val dailyForecast = repo.dailyForecast

}
