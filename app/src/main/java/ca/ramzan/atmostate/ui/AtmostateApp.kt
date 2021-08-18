package ca.ramzan.atmostate.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import ca.ramzan.atmostate.ui.theme.AtmostateTheme
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalPermissionsApi
@ExperimentalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun AtmostateApp() {
    val currentListState = rememberLazyListState()
    val hourlyListState = rememberLazyListState()
    val dailyListState = rememberLazyListState()
    val pagerState = rememberPagerState(pageCount = 3)
    val scaffoldState = rememberScaffoldState()
    AtmostateTheme {
        AtmostateNavGraph(
            currentListState = currentListState,
            hourlyListState = hourlyListState,
            dailyListState = dailyListState,
            pagerState = pagerState,
            scaffoldState = scaffoldState
        )
    }
}