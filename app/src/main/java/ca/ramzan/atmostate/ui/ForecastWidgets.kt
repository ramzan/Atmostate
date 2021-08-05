package ca.ramzan.atmostate.ui

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
fun TimeUpdated(time: Long) {
    Row {
        Text(text = "Last Updated: ", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = TimeFormatter.toDate(time))
    }
}