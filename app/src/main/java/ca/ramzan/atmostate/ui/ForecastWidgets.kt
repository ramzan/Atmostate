package ca.ramzan.atmostate.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")

fun degreeToDirection(deg: Int): String {
    return directions[(deg % 360) / 45]
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
fun Clouds(clouds: Double, visibility: Double) {
    Column {
        Row {
            Text(text = "Cloudiness: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$clouds%")
        }
        Row {
            Text(text = "Visibility: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$visibility m")
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

@Composable
fun Rain(rain: Double) {
    Row {
        Text(text = "Rain: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "$rain mm")
    }
}

@Composable
fun Snow(snow: Double) {
    Row {
        Text(text = "Snow: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "$snow mm")
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

@Composable
fun TimeUpdated(time: Long) {
    Row {
        Text(text = "Last Updated: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = TimeFormatter.toDate(time))
    }
}

@Composable
fun UVIndex(uvi: Double) {
    Row {
        Text(text = "UV Index: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "$uvi")
    }
}

@Composable
fun Wind(speed: Double, gust: Double?, direction: String) {
    Column {
        Text(text = "Wind", style = TextStyle(fontWeight = FontWeight.Bold))
        Row {
            Text(text = "Speed: ", style = TextStyle(fontWeight = FontWeight.Bold))
            Text(text = "$speed m/s $direction")
        }
        gust?.run {
            Row {
                Text(text = "Gust: ", style = TextStyle(fontWeight = FontWeight.Bold))
                Text(text = "$gust m/s")
            }
        }
    }
}