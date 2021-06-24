package ca.ramzan.atmostate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Current
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun CurrentForecast(current: Current) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            Column {
                Text(text = current.dt.toString())
                Image(
                    painter = rememberCoilPainter("https://openweathermap.org/img/wn/${current.weather[0].icon}@2x.png"),
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