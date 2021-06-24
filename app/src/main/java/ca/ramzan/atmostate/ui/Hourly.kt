package ca.ramzan.atmostate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Hourly
import ca.ramzan.atmostate.ui.theme.Lime200

@Composable
fun HourlyForecast(listState: LazyListState, hourly: List<Hourly>) {
    LazyColumn(
        state = listState,
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
                        Clouds(clouds, visibility)
                        UVIndex(uvi)
                        Wind(windSpeed, windGust, windDeg)
                        Pop(pop)
                        rain?.let { Rain(it.hour) }
                        snow?.let { Snow(it.hour) }
                        weather.forEach { Weather(it) }
                    }
                }
            }
        }
    }
}