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
import ca.ramzan.atmostate.database.weather.DbDaily
import ca.ramzan.atmostate.ui.theme.Lime200
import coil.compose.rememberImagePainter
import com.ramzan.atmostate.R

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun DailyForecast(listState: LazyListState, daily: List<DbDaily>) {
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (daily.isEmpty()) {
            item {
                Text(text = "No data", modifier = Modifier.fillMaxSize())
            }
        }
        daily.forEachIndexed { i, daily ->
            stickyHeader {
                Text(
                    text = TimeFormatter.toWeekDay(daily.date),
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
                                    text = description,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Image(
                                        painter = rememberImagePainter("https://openweathermap.org/img/wn/${icon}@4x.png"),
                                        contentDescription = "Forecast image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "High: ${tempMax}°C",
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Text(
                                        text = "Low: ${tempMin}°C",
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
                                        "${pop}%",
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
                                            text = "$windSpeed km/h ${
                                                degreeToDirection(windDeg)
                                            }"
                                        )
                                        Text(text = "Wind")
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "$windGust km/h")
                                        Text(text = "Wind Gust")
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "${humidity}%")
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
                                        Text(text = "${tempMorn}°")
                                        Text(text = "${tempDay}°")
                                        Text(text = "${tempEve}°")
                                        Text(text = "${tempNight}°")
                                    }
                                    Column {
                                        Text(text = " Feels like $feelsLikeMorn")
                                        Text(text = " Feels like $feelsLikeDay")
                                        Text(text = " Feels like $feelsLikeEve")
                                        Text(text = " Feels like $feelsLikeNight")
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