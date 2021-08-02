package ca.ramzan.atmostate.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.ramzan.atmostate.network.Hourly
import coil.compose.rememberImagePainter
import com.ramzan.atmostate.R
import java.util.*
import kotlin.math.roundToInt

@ExperimentalAnimationApi
@Composable
fun HourlyForecast(listState: LazyListState, hourly: List<Hourly>) {
    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(hourly.size) {
            hourly.forEachIndexed { i, hour ->
                val (expanded, setExpanded) = rememberSaveable(hour) { mutableStateOf(i == 0) }
                hour.run {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { setExpanded(!expanded) }
                            .padding(16.dp, 8.dp, 16.dp, 12.dp),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(TimeFormatter.toHourOfDay(dt))
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Image(
                                        painter = rememberImagePainter("https://openweathermap.org/img/wn/${weather.first().icon}@4x.png"),
                                        contentDescription = "Forecast image",
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text("${temp.roundToInt()}°", fontSize = 32.sp)
                                }
                                Text("Feels like ${feelsLike.roundToInt()}")
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
                                        "${pop.roundToInt()}%",
                                        Modifier.wrapContentWidth(Alignment.End)
                                    )
                                }
                                rain?.let { HourRain(it.hour) }
                                snow?.let { HourSnow(it.hour) }
                            }
                        }
                        AnimatedVisibility(expanded) {
                            ExpandedHour(
                                weather.first().description.capitalized(),
                                (windSpeed * 3.6).roundToInt(),
                                ((windGust ?: 0.0) * 3.6).roundToInt(),
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

@Composable
fun HourRain(rain: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.rain_drop),
            contentDescription = "Rain",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "${"%.1f".format(rain)}mm")
    }
}

@Composable
fun HourSnow(snow: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.snow_flake),
            contentDescription = "Rain",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "${"%.1f".format(snow)}mm")
    }
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}