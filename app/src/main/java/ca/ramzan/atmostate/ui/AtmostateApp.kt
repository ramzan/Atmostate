package ca.ramzan.atmostate.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.ramzan.atmostate.MainViewModel
import ca.ramzan.atmostate.repository.RefreshState
import ca.ramzan.atmostate.ui.theme.AtmostateTheme
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500
import com.google.accompanist.pager.*
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
fun AtmostateApp(vm: MainViewModel = viewModel()) {
    val refreshState = vm.state.collectAsState()
    val currentForecast = vm.currentForecast.collectAsState()
    val hourlyForecast = vm.hourlyForecast.collectAsState()
    val dailyForecast = vm.dailyForecast.collectAsState()
    val cities = vm.cities.collectAsState()
    val currentListState = rememberLazyListState()
    val hourlyListState = rememberLazyListState()
    val dailyListState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = 3)

    AtmostateTheme {
        Scaffold(
            topBar = { MainAppBar(pagerState) },
            drawerContent = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    cities.value.forEach { city ->
                        item {
                            Text(
                                text = city.name, modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    item {
                        Text(
                            text = "+ Add location",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { vm.addCity() }
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
}

@ExperimentalPagerApi
@Composable
fun MainAppBar(pagerState: PagerState) {
    val scope = rememberCoroutineScope()
    Column {
        TopAppBar(
            title = { Text("TopAppBar") },
            backgroundColor = Orange500,
            navigationIcon = {
                IconButton(onClick = {}) {
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

@ExperimentalCoroutinesApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AtmostateApp()
}