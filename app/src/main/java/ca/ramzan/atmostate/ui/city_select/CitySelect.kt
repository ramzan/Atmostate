package ca.ramzan.atmostate.ui.city_select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500

@Composable
fun CitySelect(
    vm: CitySelectViewModel,
    navController: NavController,
) {
    val cities = vm.filteredCities.collectAsState()
    val query = vm.query.collectAsState()

    Scaffold(
        topBar = { CitySelectAppBar(navController::navigateUp, query.value, vm::setQuery) },
        content = {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(
                    items = cities.value
                ) { city ->
                    Text(
                        text = city.name,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.addCity(city.id) }
                    )
                }
            }
        },
        backgroundColor = Orange100,
    )
}

@Composable
fun CitySelectAppBar(
    onBackPress: () -> Boolean,
    query: String,
    setQuery: (String) -> Unit
) {
    Column {
        TopAppBar(
            title = {
                OutlinedTextField(
                    value = query,
                    onValueChange = setQuery,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            Icon(
                                Icons.Filled.Clear,
                                contentDescription = "Clear search query",
                                modifier = Modifier.clickable { setQuery("") }
                            )
                        }
                    },
                    placeholder = {
                        Text(text = "Search for a location")
                    }
                )
            },
            backgroundColor = Orange500,
            navigationIcon = {
                IconButton(onClick = { onBackPress() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
                }
            }
        )
    }
}