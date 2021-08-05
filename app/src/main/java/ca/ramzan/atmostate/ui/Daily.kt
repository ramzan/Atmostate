package ca.ramzan.atmostate.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.ramzan.atmostate.network.Daily
import ca.ramzan.atmostate.ui.theme.Lime200
import coil.compose.rememberImagePainter
import com.ramzan.atmostate.R
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun DailyForecast(listState: LazyListState, dailyForecast: List<Daily>) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        dailyForecast.forEachIndexed { i, daily ->
            stickyHeader {
                Text(
                    text = TimeFormatter.toWeekDay(daily.dt),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Lime200, RectangleShape)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.subtitle1
                )
            }
            item {
                val (expanded, setExpanded) = rememberSaveable(daily) { mutableStateOf(i == 0) }
                daily.run {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { setExpanded(!expanded) }
                            .padding(16.dp, 8.dp, 16.dp, 12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column {
                                Text(
                                    text = weather.first().description.capitalized(),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Image(
                                        painter = rememberImagePainter("https://openweathermap.org/img/wn/${weather.first().icon}@4x.png"),
                                        contentDescription = "Forecast image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "High: ${temp.max.roundToInt()}°C",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Text(
                                        text = "Low: ${temp.min.roundToInt()}°C",
                                        fontSize = 18.sp
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.End
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
                                        "${(pop * 100).toInt()}%",
                                        Modifier.wrapContentWidth(Alignment.End)
                                    )
                                }
                                rain?.let { Rain(it) }
                                snow?.let { Snow(it) }
                            }
                        }
                        AnimatedVisibility(expanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "${(windSpeed * 3.6).roundToInt()} km/h ${
                                                degreeToDirection(windDeg)
                                            }"
                                        )
                                        Text(text = "Wind")
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "${((windGust ?: 0.0) * 3.6).roundToInt()} km/h")
                                        Text(text = "Wind Gust")
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "${humidity.roundToInt()}%")
                                        Text(text = "Humidity")
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Column {
                                        Text(text = "Morning: ", fontWeight = FontWeight.Bold)
                                        Text(text = "Day: ", fontWeight = FontWeight.Bold)
                                        Text(text = "Evening: ", fontWeight = FontWeight.Bold)
                                        Text(text = "Night: ", fontWeight = FontWeight.Bold)
                                    }
                                    Column {
                                        Text(text = "${temp.morn.roundToInt()}°")
                                        Text(text = "${temp.day.roundToInt()}°")
                                        Text(text = "${temp.eve.roundToInt()}°")
                                        Text(text = "${temp.night.roundToInt()}°")
                                    }
                                    feelsLike?.run {
                                        Column {
                                            Text(text = " Feels like ${morn.roundToInt()}")
                                            Text(text = " Feels like ${day.roundToInt()}")
                                            Text(text = " Feels like ${eve.roundToInt()}")
                                            Text(text = " Feels like ${night.roundToInt()}")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}