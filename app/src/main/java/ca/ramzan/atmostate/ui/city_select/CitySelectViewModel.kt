package ca.ramzan.atmostate.ui.city_select

import androidx.lifecycle.ViewModel
import ca.ramzan.atmostate.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CitySelectViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {
    val allCities = repo.allCities
}