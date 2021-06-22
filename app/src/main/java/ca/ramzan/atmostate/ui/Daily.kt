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
import ca.ramzan.atmostate.network.Daily
import com.google.accompanist.coil.rememberCoilPainter

@Composable
fun DailyForecast(daily: List<Daily>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(daily.size) {
            daily.forEach { day ->
                Column {
                    Text(text = day.dt.toString())
                    Image(
                        painter = rememberCoilPainter("https://openweathermap.org/img/wn/${day.weather[0].icon}@2x.png"),
                        contentDescription = "Forecast image"
                    )
                }
            }
        }
    }
}