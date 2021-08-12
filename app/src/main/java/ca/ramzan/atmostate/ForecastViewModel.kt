package ca.ramzan.atmostate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ForecastViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {
    fun refresh() {
        viewModelScope.launch {
            repo.refreshSelectedCity()
        }
    }

    fun setCurrentCity(cityId: Long) {
        viewModelScope.launch {
            repo.setCurrentCity(cityId)
        }
    }

    val currentCityname = repo.currentCity.map { it?.name ?: "" }

    val cities = repo.savedCities
    val state = repo.refreshState

    val currentForecast = repo.currentForecast
    val hourlyForecast = repo.hourlyForecast
    val dailyForecast = repo.dailyForecast

}
