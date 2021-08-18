package ca.ramzan.atmostate.ui.city_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.ramzan.atmostate.domain.Country
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

    private val _countries = MutableStateFlow<List<Country>>(emptyList())
    val countries: StateFlow<List<Country>>
        get() = _countries

    val countryIndex = repo.countryId.combine(countries) { id, countries ->
        countries.withIndex().firstOrNull { it.value.id == id }?.index ?: 0
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, 0)

    fun selectCountry(index: Int) {
        viewModelScope.launch {
            repo.setCurrentCountry(countries.value[index].id)
        }
    }

    init {
        viewModelScope.launch {
            CoroutineScope(Dispatchers.IO).launch {
                _countries.emit(repo.getAllCountries())
            }
        }
    }

    private val _query = MutableStateFlow("")
    val query: StateFlow<String>
        get() = _query

    fun setQuery(name: String) {
        viewModelScope.launch {
            _query.emit(name)
        }
    }

    private val cities = repo.citiesForCountry

    val filteredCities = cities.combine(query) { cities, query ->
        val q = query.lowercase(Locale.ROOT).trim()
        cities.filter { city ->
            city.name.lowercase(Locale.ROOT).contains(q)
        }
    }.stateIn(CoroutineScope(Dispatchers.Default), SharingStarted.Eagerly, emptyList())

    fun addCity(id: Long) = repo.addCity(id)

    fun removeCity(id: Long) = repo.removeCity(id)
}