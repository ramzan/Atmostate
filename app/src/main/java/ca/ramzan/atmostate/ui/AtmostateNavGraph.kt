package ca.ramzan.atmostate.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ca.ramzan.atmostate.ForecastViewModel
import ca.ramzan.atmostate.ui.city_select.CitySelect
import ca.ramzan.atmostate.ui.city_select.CitySelectViewModel
import ca.ramzan.atmostate.ui.forecast.Forecast
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.ExperimentalCoroutinesApi


object MainDestinations {
    const val FORECAST_ROUTE = "forecast"
    const val CITY_SELECT_ROUTE = "city_select"
}

@ExperimentalCoroutinesApi
@ExperimentalPagerApi
@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun AtmostateNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.FORECAST_ROUTE,
    forecastViewModel: ForecastViewModel = viewModel(),
    citySelectViewModel: CitySelectViewModel = viewModel(),
    currentListState: LazyListState,
    hourlyListState: LazyListState,
    dailyListState: LazyListState,
    pagerState: PagerState
) {

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(MainDestinations.FORECAST_ROUTE) {
            Forecast(
                vm = forecastViewModel,
                navController = navController,
                currentListState = currentListState,
                hourlyListState = hourlyListState,
                dailyListState = dailyListState,
                pagerState = pagerState
            )
        }
        composable(MainDestinations.CITY_SELECT_ROUTE) {
            CitySelect(
                vm = citySelectViewModel,
                navController = navController,
            )
        }
    }
}