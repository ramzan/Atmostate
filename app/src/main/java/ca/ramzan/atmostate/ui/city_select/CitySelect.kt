package ca.ramzan.atmostate.ui.city_select

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.ramzan.atmostate.domain.Country

@ExperimentalFoundationApi
@Composable
fun CitySelect(
    vm: CitySelectViewModel,
    navController: NavController,
) {
    val allCities = vm.filteredCities.collectAsState()
    val countries = vm.countries.collectAsState()
    val query = vm.query.collectAsState()
    val selectedIndex = vm.countryIndex.collectAsState()

    val (expanded, setExpanded) = rememberSaveable { mutableStateOf(false) }

    if (expanded) {
        BackHandler(onBack = { setExpanded(false) })
        CountrySelector(
            countries.value,
            selectedIndex.value,
            vm::selectCountry,
            setExpanded
        )
        return
    }
    Scaffold(
        topBar = { CitySelectAppBar(navController::navigateUp) },
        content = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                stickyHeader {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        OutlinedTextField(
                            value = countries.value[selectedIndex.value].name,
                            readOnly = true,
                            onValueChange = {},
                            label = { Text("Country") },
                            trailingIcon = {
                                Icon(
                                    Icons.Filled.ArrowDropDown,
                                    contentDescription = "Choose country",
                                    modifier = Modifier
                                        .clickable(onClick = { setExpanded(true) })
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor = MaterialTheme.colors.onSurface.copy(ContentAlpha.medium)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                        SearchBox(query.value, vm::setQuery)
                    }
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
        }
    )
}

@Composable
fun CitySelectAppBar(onBackPress: () -> Boolean) {
    Column {
        TopAppBar(
            title = {
                Text("Select location")
            },
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
            .padding(horizontal = 16.dp)
            .focusRequester(requester)
    )
    SideEffect {
        requester.requestFocus()
    }
}

@Composable
fun CountrySelector(
    countries: List<Country>,
    selectedIndex: Int,
    setSelectedIndex: (Int) -> Unit,
    setExpanded: (Boolean) -> Unit
) {
    val state = rememberLazyListState()
    LazyColumn(
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(items = countries) { i, country ->
            Text(
                text = country.name,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .background(
                        if (i == selectedIndex) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background
                    )
                    .clickable {
                        setSelectedIndex(i)
                        setExpanded(false)
                    }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .fillMaxWidth()
            )
        }
    }
    LaunchedEffect(key1 = 'a') {
        state.animateScrollToItem(selectedIndex)
    }
}

@Composable
fun BackHandler(onBack: () -> Unit) {
    val backDispatcher =
        LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher ?: return
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnBack by rememberUpdatedState(onBack)
    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = currentOnBack()
        }
    }
    backCallback.isEnabled = true

    DisposableEffect(lifecycleOwner, backDispatcher) {
        backDispatcher.addCallback(lifecycleOwner, backCallback)
        onDispose {
            backCallback.remove()
        }
    }
}
