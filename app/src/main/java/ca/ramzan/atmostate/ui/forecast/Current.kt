package ca.ramzan.atmostate.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.domain.Alert
import ca.ramzan.atmostate.domain.Current
import coil.compose.rememberImagePainter
import java.time.ZonedDateTime
import com.ramzan.atmostate.R as AtmostateR

private val gridPadding = 24.dp

@Composable
fun CurrentForecast(listState: LazyListState, current: Current?, alerts: List<Alert>) {
    if (current == null) {
        NoDataMessage(message = "Sorry, the current conditions are unavailable at this time. Please try again later.")
        return
    }

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
    ) {
        current.run {
            item { TimeUpdated(lastUpdated) }
            item {
                Weather(
                    description,
                    icon,
                    temp,
                    feelsLike,
                    uvi
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    GridColumn {
                        Wind(
                            windSpeed,
                            windGust,
                            degreeToDirection(windDeg)
                        )
                        Cloudiness(clouds)
                    }
                    GridColumn {
                        Humidity("${humidity}%")
                        Sunrise(sunrise)

                    }
                    GridColumn {
                        Pressure("${"%.1f".format(pressure)}kPa")
                        Sunset(sunset)

                    }
                    GridColumn {
                        Visibility(visibility)
                        DewPoint("${dewPoint}°C")
                    }

                }
            }
        }
        items(
            items = alerts
        ) { alert ->
            Alert(alert)
        }
    }
}

@Composable
fun Weather(description: String, icon: String, temp: Int, feelsLike: Int, uvi: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberImagePainter("https://openweathermap.org/img/wn/${icon}@4x.png"),
            contentDescription = "Forecast image",
            modifier = Modifier.size(128.dp)
        )
        Column {
            Text(text = "$temp°C", style = MaterialTheme.typography.h3)
            Text(text = "Feels like $feelsLike")
            Text(description)
            Text("UV Index: $uvi")
        }
    }
}

@Composable
fun GridColumn(content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxHeight()
    ) {
        content()
    }
}

@Composable
fun GridItem(padded: Boolean = false, content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(bottom = if (padded) gridPadding else 0.dp)
    ) {
        content()
    }
}

@Composable
fun Wind(speed: Int, gust: Int, direction: String) {
    GridItem {
        Image(
            painter = painterResource(AtmostateR.drawable.wind),
            contentDescription = "Wind"
        )
        Text(text = "Wind", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = "${speed}km/h $direction")
        Text(text = "${gust}km/h")
    }
}

@Composable
fun Cloudiness(clouds: Int) {
    GridItem {
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
    GridItem(padded = true) {
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
    GridItem(padded = true) {
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
    GridItem(padded = true) {
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
    GridItem {
        Image(
            painter = painterResource(AtmostateR.drawable.dewpoint),
            contentDescription = "Dew point"
        )
        Text(text = "Dew point", style = TextStyle(fontWeight = FontWeight.Light))
        Text(dewPoint)
    }
}

@Composable
fun Sunrise(sunrise: ZonedDateTime) {
    GridItem {
        Image(
            painter = painterResource(AtmostateR.drawable.sunrise),
            contentDescription = "Sunrise"
        )
        Text(text = "Sunrise", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = TimeFormatter.toDayHour(sunrise))
    }
}

@Composable
fun Sunset(sunset: ZonedDateTime) {
    GridItem {
        Image(
            painter = painterResource(AtmostateR.drawable.sunset),
            contentDescription = "Sunset"
        )
        Text(text = "Sunset", style = TextStyle(fontWeight = FontWeight.Light))
        Text(text = TimeFormatter.toDayHour(sunset))
    }
}

@Composable
fun Alert(alert: Alert) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.error)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text = alert.event)
        Text(text = "Start: ${TimeFormatter.toDate(alert.start)}")
        Text(text = "End: ${TimeFormatter.toDate(alert.end)}")
        Text(text = alert.senderName)
        Text(text = alert.description)
    }
}