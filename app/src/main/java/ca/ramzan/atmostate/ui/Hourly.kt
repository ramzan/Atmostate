package ca.ramzan.atmostate.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Hourly
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun HourlyForecast(hourly: List<Hourly>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(hourly.size) {
            hourly.forEach { hour ->
                Column {
                    Text(text = hour.dt.toString())
                    Image(
                        painter = rememberCoilPainter("https://openweathermap.org/img/wn/${hour.weather[0].icon}@2x.png"),
                        contentDescription = "Forecast image"
                    )
                }
            }
        }
    }
}