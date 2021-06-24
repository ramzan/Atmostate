package ca.ramzan.atmostate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Hourly
import ca.ramzan.atmostate.ui.theme.Lime200

@Composable
fun HourlyForecast(hourly: List<Hourly>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp)
    ) {
        items(hourly.size) {
            hourly.forEach { hour ->
                hour.run {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 16.dp)
                            .background(Lime200, RectangleShape)
                            .padding(12.dp)
                    ) {
                        TimeUpdated(dt)
                        Temperature(temp, feelsLike)
                        AirInfo(pressure, humidity, dewPoint)
                        Clouds(clouds, uvi, visibility)
                        Wind(windSpeed, windGust, windDeg)
                        Pop(pop)
                        rain?.let { Rain(it) }
                        snow?.let { Snow(it) }
                        weather.forEach { Weather(it) }
                    }
                }
            }
        }
    }
}

@Composable
fun Pop(pop: Double) {
    Row {
        Text(text = "POP: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "$pop%")
    }
}