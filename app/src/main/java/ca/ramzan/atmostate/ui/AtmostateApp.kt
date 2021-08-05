package ca.ramzan.atmostate.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.ramzan.atmostate.MainState
import ca.ramzan.atmostate.MainViewModel
import ca.ramzan.atmostate.ui.theme.AtmostateTheme
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch

val tabTitles = listOf("Current", "Hourly", "Daily")

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@ExperimentalPagerApi
@Composable
fun AtmostateApp(vm: MainViewModel = viewModel()) {
    val state = vm.state.collectAsState()
    val currentListState = rememberLazyListState()
    val hourlyListState = rememberLazyListState()
    val dailyListState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = 3)

    AtmostateTheme {
        Scaffold(
            topBar = { MainAppBar(pagerState) },
            drawerContent = { Text(text = "drawerContent") },
            content = {
                when (val s = state.value) {
                    is MainState.Error -> CenteredItem {
                        Text(text = s.error, textAlign = TextAlign.Center)
                    }
                    is MainState.Loaded -> {
                        HorizontalPager(state = pagerState) { page ->
                            when (page) {
                                0 -> CurrentForecast(currentListState, s.data.current)
                                1 -> HourlyForecast(hourlyListState, s.data.hourly)
                                2 -> DailyForecast(dailyListState, s.data.daily)
                            }
                        }
                    }
                    is MainState.Loading -> CenteredItem { CircularProgressIndicator() }
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

@Composable
fun CenteredItem(content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) { content() }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@ExperimentalPagerApi
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AtmostateApp()
}