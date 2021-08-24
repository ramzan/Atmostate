package ca.ramzan.atmostate.ui.forecast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.ramzan.atmostate.domain.Daily
import coil.compose.rememberImagePainter
import com.ramzan.atmostate.R

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun DailyForecast(listState: LazyListState, daily: List<Daily>) {
    if (daily.isEmpty()) {
        NoDataMessage(message = stringResource(R.string.no_data_message_daily))
        return
    }
    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        daily.forEachIndexed { i, daily ->
            stickyHeader {
                StickyListHeader(text = TimeFormatter.toWeekDay(daily.date))
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
                                    fontWeight = FontWeight.Normal
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Image(
                                        painter = rememberImagePainter(
                                            stringResource(
                                                R.string.icon_url,
                                                icon
                                            )
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.temp_max, tempMax),
                                        fontSize = 18.sp,
                                        modifier = Modifier.padding(end = 16.dp)
                                    )
                                    Text(
                                        text = stringResource(R.string.temp_min, tempMin),
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
                                        contentDescription = stringResource(R.string.probability_of_precipitation),
                                        modifier = Modifier.padding(end = 8.dp)
                                    )
                                    Text(
                                        stringResource(R.string.int_percent, pop),
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
                                            text = stringResource(
                                                R.string.wind_speed,
                                                windSpeed,
                                                degreeToDirection(windDeg)
                                            )
                                        )
                                        Text(text = stringResource(R.string.wind))
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = stringResource(
                                                R.string.wind_speed_gust,
                                                windGust
                                            )
                                        )
                                        Text(text = stringResource(R.string.wind_gust))
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = stringResource(R.string.int_percent, humidity))
                                        Text(text = stringResource(R.string.humidity))
                                    }
                                }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(end = 16.dp)) {
                                        Text(stringResource(R.string.morning))
                                        Text(stringResource(R.string.day))
                                        Text(stringResource(R.string.evening))
                                        Text(stringResource(R.string.night))
                                    }
                                    Column {
                                        DailyTemp(tempMorn, feelsLikeMorn)
                                        DailyTemp(tempDay, feelsLikeDay)
                                        DailyTemp(tempEve, feelsLikeEve)
                                        DailyTemp(tempNight, feelsLikeNight)
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

@Composable
fun DailyTemp(temp: Int, feelsLike: Int?) {
    Text(stringResource(R.string.temp_and_feels_like, temp, feelsLike ?: temp))
}