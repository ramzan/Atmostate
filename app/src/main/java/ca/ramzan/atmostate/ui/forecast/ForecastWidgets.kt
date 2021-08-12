package ca.ramzan.atmostate.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ramzan.atmostate.R
import java.util.*

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

@Composable
fun Rain(rain: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.rain_drop),
            contentDescription = "Rain",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "${"%.1f".format(rain)}mm")
    }
}

@Composable
fun Snow(snow: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.snow_flake),
            contentDescription = "Rain",
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = "${"%.1f".format(snow)}mm")
    }
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}