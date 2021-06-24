package ca.ramzan.atmostate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Current
import ca.ramzan.atmostate.network.Precipitation
import ca.ramzan.atmostate.network.Weather
import com.google.accompanist.coil.rememberCoilPainter

@Preview
@Composable
fun CurrentForecastPrev() {
    CurrentForecast(
        current = Current(
            1L,
            2L,
            3L,
            1.0,
            2.0,
            3.0,
            4.0,
            5.0,
            6.0,
            7.0,
            8.0,
            9.0,
            10.0,
            11,
            Precipitation(12.0),
            Precipitation(13.0),
            listOf(Weather("a", "b", "c", "d"))
        )
    )
}

@Composable
fun CurrentForecast(current: Current) {
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        current.run {
            item { TimeUpdated(dt) }
            if (sunrise != 0L) item { SunriseSunset(sunrise, sunset) }
            item { Temperature(temp, feelsLike) }
            item { AirInfo(pressure, humidity, dewPoint) }
            item { Clouds(clouds, uvi, visibility) }
            if (windSpeed != 0.0) item { Wind(windSpeed, windGust, windDeg) }
            rain?.let { item { Rain(it) } }
            snow?.let { item { Snow(it) } }
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
fun Clouds(clouds: Double, uvi: Double, visibility: Double) {
    Column {
        Row {
            Text(text = "Cloudiness: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$clouds%")
        }
        Row {
            Text(text = "UV Index: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$uvi")
        }
        Row {
            Text(text = "Visibility: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$visibility m")
        }
    }
}

@Composable
fun Wind(speed: Double, gust: Double?, deg: Int) {
    Column {
        Text(text = "Wind", style = TextStyle(fontWeight = FontWeight.Bold))
        Row {
            Text(text = "Speed: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$speed m/s")
        }
        gust?.run {
            Row {
                Text(text = "Gust: ", style = TextStyle(fontWeight = FontWeight.Bold))
                Text(text = "$gust m/s")
            }
        }
        Row {
            Text(text = "Direction: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$deg°")
        }
    }
}

@Composable
fun Rain(rain: Precipitation) {
    Row {
        Text(text = "Rain: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "${rain.hour} mm")
    }
}

@Composable
fun Snow(snow: Precipitation) {
    Row {
        Text(text = "Snow: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "${snow.hour} mm")
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
fun TimeUpdated(time: Long) {
    Row {
        Text(text = "Last Updated: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "$time")
    }
}

@Composable
fun AirInfo(pressure: Double, humidity: Double, dewPoint: Double) {
    Column {
        Row {
            Text(text = "Pressure: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$pressure hPa")
        }
        Row {
            Text(text = "Humidity: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$humidity%")
        }
        Row {
            Text(text = "Dew Point: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$dewPoint K")
        }
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

@Composable
fun Temperature(temp: Double, feelsLike: Double) {
    Column {
        Row {
            Text(text = "Temperature: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$temp K")
        }
        Row {
            Text(text = "Feels like: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$feelsLike K")
        }
    }
}

//val dt: Long,
//val sunrise: Long,
//val sunset: Long,
//val temp: Double,
//@Json(name = "feels_like") val feelsLike: Double,
//val pressure: Double,
//val humidity: Double,
//@Json(name = "dew_point") val dewPoint: Double,
//val clouds: Double,
//val uvi: Double,
//val visibility: Double,
//@Json(name = "wind_speed") val windSpeed: Double,
//@Json(name = "wind_gust") val windGust: Double?,
//@Json(name = "wind_deg") val windDeg: Double,
//val rain: Precipitation?,
//val snow: Precipitation?,
//val weather: List<Weather>