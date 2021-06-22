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
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.WeatherResult
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun CurrentForecast(data: WeatherResult.Success) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            LocationInfo(
                lat = data.lat,
                lon = data.lon,
                tz = data.timezone,
                tzOffset = data.timezone_offset
            )
        }
        item {
            Row {
                Text(text = data.current.weather.toString())
                Image(
                    painter = rememberCoilPainter("https://openweathermap.org/img/wn/${data.current.weather[0].icon}@2x.png"),
                    contentDescription = "Forecast image"
                )
            }
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