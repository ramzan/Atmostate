package ca.ramzan.atmostate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Current
import ca.ramzan.atmostate.network.Weather
import com.google.accompanist.coil.rememberCoilPainter
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt
import com.ramzan.atmostate.R as AtmostateR

@Composable
fun CurrentForecast(listState: LazyListState, current: Current) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        current.run {
            item { TimeUpdated(dt) }
            item { Weather(weather.first(), temp.roundToInt(), feelsLike.roundToInt()) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Wind(
                        (windSpeed * 1000 / 3600).roundToInt(),
                        windGust?.let { (it * 1000 / 3600).roundToInt() },
                        degreeToDirection(windDeg)
                    )
                    Humidity("${humidity.roundToInt()}%")
                    Pressure("${"%.1f".format(pressure / 10)}kPa")
                    Visibility((visibility / 1000).toInt())
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Cloudiness(clouds.toInt())
                    Sunrise(sunrise)
                    Sunset(sunset)
                    DewPoint("${dewPoint.roundToInt()}°C")
                }
            }
            item { UVIndex(uvi) }
            rain?.let { item { Rain(it.hour) } }
            snow?.let { item { Snow(it.hour) } }
        }
    }
}

@Composable
fun Wind(speed: Int, gust: Int?, direction: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.wind),
            contentDescription = "Wind"
        )
        Text(text = "Wind", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = "${speed}km/h $direction")
        gust?.run {
            Text(text = "$gust m/s")
        }
    }
}

@Composable
fun Cloudiness(clouds: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.cloud),
            contentDescription = "Cloud"
        )
        Text(text = "Cloudiness", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = "$clouds%")
    }
}

@Composable
fun Visibility(visibility: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.telescope),
            contentDescription = "Telescope"
        )
        Text(text = "Visibility", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = "${visibility}km")
    }
}

@Composable
fun Pressure(pressure: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.barometer),
            contentDescription = "Barometer"
        )
        Text(text = "Pressure", style = TextStyle(fontWeight = FontWeight.Light))
        Text(pressure)
    }
}

@Composable
fun Humidity(humidity: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.humidity),
            contentDescription = "Humidity"
        )
        Text(text = "Humidity", style = TextStyle(fontWeight = FontWeight.Light))
        Text(humidity)
    }
}

@Composable
fun DewPoint(dewPoint: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.dewpoint),
            contentDescription = "Dew point"
        )
        Text(text = "Dew point", style = TextStyle(fontWeight = FontWeight.Light))
        Text(dewPoint)
    }
}

@Composable
fun LocationInfo(lat: Double, lon: Double, tz: String, tzOffset: Long) {
    Column {
        Text(text = "Location", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = "Coordinates: $lat, $lon")
        Text(text = "Time zone: $tz")
        Text(text = "Time zone offset: $tzOffset")
    }
}

@Composable
fun Weather(weather: Weather, temp: Int, feelsLike: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = rememberCoilPainter("https://openweathermap.org/img/wn/${weather.icon}@4x.png"),
                contentDescription = "Forecast image"
            )
            Text(text = "$temp°C", style = MaterialTheme.typography.h3)
        }
        Text(text = "Feels like $feelsLike°C")
        Text(weather.description.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() })
    }
}

@Composable
fun Sunrise(sunrise: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.sunrise),
            contentDescription = "Sunrise"
        )
        Text(text = "Sunrise", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = TimeFormatter.toDayHour(sunrise))
    }
}

@Composable
fun Sunset(sunset: Long) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(AtmostateR.drawable.sunset),
            contentDescription = "Sunset"
        )
        Text(text = "Sunset", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = TimeFormatter.toDayHour(sunset))
    }
}

object TimeFormatter {
    private val format = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())

    fun toDayHour(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            format.format(this)
        }
    }

    fun toDate(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this)
        }
    }
}