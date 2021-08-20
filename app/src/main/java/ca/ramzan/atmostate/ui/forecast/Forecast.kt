package ca.ramzan.atmostate.ui.forecast

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import ca.ramzan.atmostate.ForecastViewModel
import ca.ramzan.atmostate.repository.RefreshState
import ca.ramzan.atmostate.repository.USER_LOCATION_CITY_ID
import ca.ramzan.atmostate.ui.MainDestinations
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val tabTitles = listOf("Current", "Hourly", "Daily")


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
val HIDE_LOCATION_RATIONALE = booleanPreferencesKey("hide_rationale")

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun Forecast(
    vm: ForecastViewModel,
    navController: NavController,
    currentListState: LazyListState,
    hourlyListState: LazyListState,
    dailyListState: LazyListState,
    pagerState: PagerState,
    scaffoldState: ScaffoldState,
) {
    val refreshState = vm.state.collectAsState()
    val currentForecast = vm.currentForecast.collectAsState()
    val hourlyForecast = vm.hourlyForecast.collectAsState()
    val dailyForecast = vm.dailyForecast.collectAsState()
    val alerts = vm.alerts.collectAsState()
    val cities = vm.cities.collectAsState()
    val currentCityName = vm.currentCityName.collectAsState("")
    val context = LocalContext.current
    val hideRationale =
        context.dataStore.data.map { it[HIDE_LOCATION_RATIONALE] ?: false }.collectAsState(true)

    val scope = rememberCoroutineScope()

    val (errorShown, setErrorShown) = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ForecastAppBar(
                pagerState,
                currentCityName.value,
                {
                    scope.launch {
                        scaffoldState.drawerState.open()
                    }
                },
                {
                    scope.launch {
                        vm.removeCurrentCity()
                        scaffoldState.snackbarHostState.showSnackbar("Location removed")
                    }
                },
                { showSourceCode(context) }
            )
        },
        drawerContent = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colors.background)
            ) {
                item {
                    DrawerItem(text = "Your location", selected = currentCityName.value.isEmpty()) {
                        vm.setCurrentCity(USER_LOCATION_CITY_ID)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                }
                items(cities.value) { city ->
                    DrawerItem(text = city.name, selected = city.selected) {
                        vm.setCurrentCity(city.id)
                        scope.launch { scaffoldState.drawerState.close() }
                    }
                }
                item {
                    DrawerItem(text = "+ Add location", selected = false) {
                        navController.navigate(MainDestinations.CITY_SELECT_ROUTE)
                    }
                }
            }
        },
        content = {
            HorizontalPager(state = pagerState) { page ->
                SwipeRefresh(
                    state = rememberSwipeRefreshState(refreshState.value == RefreshState.Loading),
                    onRefresh = {
                        setErrorShown(false)
                        vm.refresh()
                    }
                ) {
                    if (currentCityName.value.isEmpty() && refreshState.value == RefreshState.PermissionError) {
                        LocationRequestScreen(
                            { navController.navigate(MainDestinations.CITY_SELECT_ROUTE) },
                            { showAppSettingsPage(context) },
                            { setLocationRationaleHidden(scope, context) },
                            vm::onPermissionGranted,
                            hideRationale.value
                        )
                    } else if (refreshState.value == RefreshState.Loading && currentForecast.value == null) {
                        LazyColumn(content = {}, modifier = Modifier.fillMaxSize())
                    } else {
                        when (page) {
                            0 -> CurrentForecast(
                                currentListState,
                                currentForecast.value,
                                alerts.value
                            )
                            1 -> HourlyForecast(hourlyListState, hourlyForecast.value)
                            2 -> DailyForecast(dailyListState, dailyForecast.value)
                        }
                    }
                }
            }
        }
    )
    if (!errorShown) {
        setErrorShown(showErrorMessage(refreshState.value, scope, scaffoldState))
    }
}

fun showErrorMessage(
    refreshState: RefreshState,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState
): Boolean {
    var shown = false
    (refreshState as? RefreshState.Error)?.run {
        scaffoldState.snackbarHostState.run {
            // Prevent duplicate snackbars when loading multiple cities
            if (currentSnackbarData?.message != message) {
                scope.launch { showSnackbar(message) }
                shown = true
            }
        }
    }
    return shown
}

@ExperimentalPagerApi
@Composable
fun ForecastAppBar(
    pagerState: PagerState,
    currentCityName: String,
    openDrawer: () -> Unit,
    removeLocation: () -> Unit,
    showSource: () -> Unit
) {
    val (showMenu, setShowMenu) = rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    Column {
        TopAppBar(
            title = { Text(if (currentCityName.isEmpty()) "Your location" else currentCityName) },
            backgroundColor = MaterialTheme.colors.primary,
            navigationIcon = {
                IconButton(onClick = { openDrawer() }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            },
            actions = {
                IconButton(onClick = { setShowMenu(true) }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { setShowMenu(false) }
                ) {
                    if (currentCityName.isNotEmpty()) {
                        DropdownMenuItem(onClick = {
                            removeLocation()
                            setShowMenu(false)
                        }) {
                            Text("Remove location")
                        }
                    }
                    DropdownMenuItem(onClick = {
                        showSource()
                        setShowMenu(false)
                    }) {
                        Text("Source code")
                    }
                }
            }
        )
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabTitles.mapIndexed { idx, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.scrollToPage(idx) } }
                )
            }
        }
    }
}

@ExperimentalPermissionsApi
@Composable
private fun LocationRequestScreen(
    navigateToSearchScreen: () -> Unit,
    navigateToSettingsScreen: () -> Unit,
    hideRationale: () -> Unit,
    onPermissionGranted: () -> Unit,
    doNotShowRationaleInit: Boolean
) {
    val (doNotShowRationale, setDoNotShowRationale) = rememberSaveable {
        mutableStateOf(
            doNotShowRationaleInit
        )
    }

    val permissionState =
        rememberPermissionState(android.Manifest.permission.ACCESS_COARSE_LOCATION)

    when {
        permissionState.hasPermission -> {
            onPermissionGranted()
            Text("Location permission Granted")
        }
        permissionState.shouldShowRationale || !permissionState.permissionRequested -> {
            if (doNotShowRationale) {
                hideRationale()
                PermissionDenied(navigateToSettingsScreen, navigateToSearchScreen)
            } else {
                AskPermission(
                    permissionState,
                    { setDoNotShowRationale(true) },
                    navigateToSearchScreen
                )
            }
        }
        else -> {
            hideRationale()
            PermissionDenied(navigateToSettingsScreen, navigateToSearchScreen)
        }
    }
}

@ExperimentalPermissionsApi
@Composable
fun AskPermission(
    permissionState: PermissionState,
    setDoNotShowRationale: () -> Unit,
    navigateToSearchScreen: () -> Unit
) {
    Column {
        Text("Would you like to automatically see weather for your location? This requires access to your location.")
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Yes")
            }
            Spacer(Modifier.width(8.dp))
            Button(onClick = setDoNotShowRationale) {
                Text("No thanks")
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Or find your location manually")
        Button(onClick = navigateToSearchScreen) {
            Text("Search")
        }
    }
}

@Composable
fun PermissionDenied(navigateToSettingsScreen: () -> Unit, navigateToSearchScreen: () -> Unit) {
    Column {
        Text(
            "Location permission denied. To use this feature, please grant location access on the Settings screen."
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = navigateToSettingsScreen) {
            Text("Open Settings")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("Or find your location manually")
        Button(onClick = navigateToSearchScreen) {
            Text("Search")
        }
    }
}

fun showAppSettingsPage(context: Context) {
    startActivity(
        context,
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
        },
        null
    )
}

fun setLocationRationaleHidden(scope: CoroutineScope, context: Context) {
    scope.launch {
        context.dataStore.edit {
            it[HIDE_LOCATION_RATIONALE] = true
        }
    }
}

fun showSourceCode(context: Context) {
    startActivity(
        context,
        Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ramzan/Atmostate")),
        null
    )
}

@Composable
fun DrawerItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .background(if (selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}