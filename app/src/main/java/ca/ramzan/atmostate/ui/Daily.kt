package ca.ramzan.atmostate.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.ramzan.atmostate.network.Daily
import ca.ramzan.atmostate.ui.theme.Lime200

@Composable
fun DailyForecast(listState: LazyListState, daily: List<Daily>) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        items(daily.size) {
            daily.forEach { day ->
                day.run {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(0.dp, 0.dp, 0.dp, 16.dp)
                            .background(Lime200, RectangleShape)
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TimeUpdated(dt)
                        SunriseSunset(sunrise, sunset)
                        MoonInfo(moonrise, moonset, moonPhase)
                        DailyTemperature(temp, feelsLike)
                        AirInfo(pressure, humidity, dewPoint)
                        Wind(windSpeed, windGust, windDeg)
                        UVIndex(uvi)
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
fun MoonInfo(moonrise: Long, moonset: Long, moonPhase: Double) {
    Column {
        Row {
            Text(text = "Moonrise: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$moonrise")
        }
        Row {
            Text(text = "Moonset: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$moonset")
        }
        Row {
            Text(text = "Moon phase: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$moonPhase")
        }
    }
}

@Composable
fun DailyTemperature(temp: Daily.Temp, feelsLike: Daily.FeelsLike?) {
    Text(text = "Temperature", style = TextStyle(fontWeight = FontWeight.Bold))
    Column {
        Row {
            Text(text = "Morning: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.morn} K")
            if (feelsLike != null) Text(text = "feels like${feelsLike.morn} K")
        }
        Row {
            Text(text = "Day: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.day} K")
            if (feelsLike != null) Text(text = "feels like${feelsLike.day} K")
        }
        Row {
            Text(text = "Evening: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.eve} K")
            if (feelsLike != null) Text(text = "feels like${feelsLike.eve} K")
        }
        Row {
            Text(text = "Night: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.night} K")
            if (feelsLike != null) Text(text = "feels like${feelsLike.night} K")
        }
        Row {
            Text(text = "Min: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.min} K")
        }
        Row {
            Text(text = "Max: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "${temp.max} K")
        }
    }
}
