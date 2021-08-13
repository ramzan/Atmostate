package ca.ramzan.atmostate.ui.city_select

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
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
    val allCities = vm.filteredCities.collectAsState()
    val query = vm.query.collectAsState()

    Scaffold(
        topBar = { CitySelectAppBar(navController::navigateUp, query.value, vm::setQuery) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    items = allCities.value
                ) { city ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (city.saved) {
                                    vm.removeCity(city.id)
                                } else {
                                    vm.addCity(city.id)
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp))
                    {
                        Text(
                            text = city.name,
                            modifier = Modifier.weight(1f)
                        )
                        if (city.saved) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "City saved",
                            )
                        }
                    }
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