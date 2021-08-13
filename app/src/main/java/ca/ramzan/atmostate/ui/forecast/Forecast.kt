package ca.ramzan.atmostate.ui.forecast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ca.ramzan.atmostate.ForecastViewModel
import ca.ramzan.atmostate.repository.RefreshState
import ca.ramzan.atmostate.ui.MainDestinations
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

val tabTitles = listOf("Current", "Hourly", "Daily")

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
    val cities = vm.cities.collectAsState()
    val currentCityName = vm.currentCityName.collectAsState("")

    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            ForecastAppBar(
                pagerState,
                currentCityName.value
            ) {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }
        },
        drawerContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                cities.value.forEach { city ->
                    item {
                        Text(
                            text = city.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    vm.setCurrentCity(city.id)
                                    scope.launch { scaffoldState.drawerState.close() }
                                }
                                .background(if (city.selected) MaterialTheme.colors.primaryVariant else MaterialTheme.colors.surface)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
                item {
                    Text(
                        text = "+ Add location",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate(MainDestinations.CITY_SELECT_ROUTE) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        },
        content = {
            HorizontalPager(state = pagerState) { page ->
                SwipeRefresh(
                    state = rememberSwipeRefreshState(refreshState.value == RefreshState.Loading),
                    onRefresh = { vm.refresh() }
                ) {
                    Box {
                        when (page) {
                            0 -> CurrentForecast(currentListState, currentForecast.value)
                            1 -> HourlyForecast(hourlyListState, hourlyForecast.value)
                            2 -> DailyForecast(dailyListState, dailyForecast.value)
                        }
                    }
                }
            }
        },
        backgroundColor = Orange100,
    )
}

@ExperimentalPagerApi
@Composable
fun ForecastAppBar(pagerState: PagerState, currentCityName: String, openDrawer: () -> Unit) {
    val scope = rememberCoroutineScope()
    Column {
        TopAppBar(
            title = { Text(currentCityName) },
            backgroundColor = Orange500,
            navigationIcon = {
                IconButton(onClick = { openDrawer() }) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
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