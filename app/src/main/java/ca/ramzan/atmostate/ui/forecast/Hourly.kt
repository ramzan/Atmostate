package ca.ramzan.atmostate.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.ramzan.atmostate.domain.Hourly
import ca.ramzan.atmostate.ui.theme.Lime200
import ca.ramzan.atmostate.ui.theme.Orange500
import coil.compose.rememberImagePainter
import com.ramzan.atmostate.R

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun HourlyForecast(listState: LazyListState, hourly: List<Hourly>) {
    if (hourly.isEmpty()) {
        NoDataMessage(message = "Sorry, the hourly forecast is unavailable at this time. Please try again later.")
        return
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        hourly.forEachIndexed { i, hour ->
            hour.run {
                if (TimeFormatter.isMidnight(hour.time)) {
                    stickyHeader {
                        StickyListHeader(text = TimeFormatter.toWeekDay(hour.time))
                    }
                }
                item {
                    val (expanded, setExpanded) = rememberSaveable(hour) { mutableStateOf(i == 0) }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { setExpanded(!expanded) }
                            .background(MaterialTheme.colors.primary.copy(alpha))
                            .padding(start = 16.dp, top = 10.dp, end = 16.dp, bottom = 14.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(TimeFormatter.toHourOfDay(time), modifier = Modifier.weight(1.0f))
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Image(
                                        painter = rememberImagePainter("https://openweathermap.org/img/wn/${icon}@4x.png"),
                                        contentDescription = "Forecast image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text("${temp}°", fontSize = 32.sp)
                                }
                                Text("Feels like $feelsLike")
                            }

                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(1.0f)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.rain_cloud),
                                        contentDescription = "Probability of precipitation",
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        "${pop}%",
                                        Modifier.wrapContentWidth(Alignment.End)
                                    )
                                }
                                rain?.let { Rain(it) }
                                snow?.let { Snow(it) }
                            }
                        }
                        AnimatedVisibility(expanded) {
                            ExpandedHour(
                                description,
                                windSpeed,
                                windGust,
                                degreeToDirection(windDeg)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedHour(forecast: String, windSpeed: Int, windGust: Int, windDirection: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = forecast)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$windSpeed km/h $windDirection")
                Text(text = "Wind")
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "$windGust km/h")
                Text(text = "Wind Gust")
            }
        }
    }
}