package ca.ramzan.atmostate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Current
import ca.ramzan.atmostate.network.Weather
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun CurrentForecast(listState: LazyListState, current: Current) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        current.run {
            item { TimeUpdated(dt) }
            if (sunrise != 0L) item { SunriseSunset(sunrise, sunset) }
            item { Temperature(temp, feelsLike) }
            item { AirInfo(pressure, humidity, dewPoint) }
            item { Clouds(clouds, visibility) }
            item { UVIndex(uvi) }
            item { Wind(windSpeed, windGust, degreeToDirection(windDeg)) }
            rain?.let { item { Rain(it.hour) } }
            snow?.let { item { Snow(it.hour) } }
            items(weather.size) { weather.forEach { Weather(it) } }
        }
    }
}

@Composable
fun LocationInfo(lat: Double, lon: Double, tz: String, tzOffset: Long) {
    Column {
        Text(text = "Location", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "Coordinates: $lat, $lon")
        Text(text = "Time zone: $tz")
        Text(text = "Time zone offset: $tzOffset")
    }
}

@Composable
fun Weather(weather: Weather) {
    Column {
        Text(text = "Weather", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(weather.id)
        Text(weather.main)
        Text(weather.description)
        Image(
            painter = rememberCoilPainter("https://openweathermap.org/img/wn/${weather.icon}@2x.png"),
            contentDescription = "Forecast image"
        )
    }
}

@Composable
fun SunriseSunset(sunrise: Long, sunset: Long) {
    Column {
        Row {
            Text(text = "Sunrise: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$sunrise")
        }
        Row {
            Text(text = "Sunset: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$sunset")
        }
    }
}