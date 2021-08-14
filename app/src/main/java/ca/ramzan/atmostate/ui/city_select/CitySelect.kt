package ca.ramzan.atmostate.ui.city_select

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500

@ExperimentalFoundationApi
@Composable
fun CitySelect(
    vm: CitySelectViewModel,
    navController: NavController,
) {
    val allCities = vm.filteredCities.collectAsState()
    val query = vm.query.collectAsState()

    Scaffold(
        topBar = { CitySelectAppBar(navController::navigateUp) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                stickyHeader {
                    SearchBox(query.value, vm::setQuery)
                }
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
fun CitySelectAppBar(onBackPress: () -> Boolean) {
    Column {
        TopAppBar(
            title = {
                Text("Select location")
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

@Composable
fun SearchBox(query: String, setQuery: (String) -> Unit) {
    val requester = FocusRequester()
    OutlinedTextField(
        value = query,
        onValueChange = setQuery,
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
        },
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .focusRequester(requester)
    )
    SideEffect {
        requester.requestFocus()
    }
}