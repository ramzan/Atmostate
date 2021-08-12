package ca.ramzan.atmostate.ui.city_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CitySelectViewModel @Inject constructor(
    private val repo: WeatherRepository
) : ViewModel() {

    private val allCities = repo.allCities

    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query

    fun setQuery(name: String) {
        viewModelScope.launch {
            _query.emit(name)
        }
    }

    val filteredCities = allCities.combine(query) { cities, query ->
        val q = query.lowercase(Locale.ROOT).trim()
        cities.filter { city ->
            city.name.lowercase(Locale.ROOT).contains(q)
        }
    }.stateIn(CoroutineScope(Dispatchers.IO), SharingStarted.Eagerly, emptyList())

    fun addCity(id: Long) {
        repo.addCity(id)
    }
}