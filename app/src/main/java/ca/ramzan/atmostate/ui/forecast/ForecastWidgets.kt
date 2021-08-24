package ca.ramzan.atmostate.ui.forecast

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ramzan.atmostate.R
import java.time.ZonedDateTime

val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")

fun degreeToDirection(deg: Int): String {
    return directions[(deg % 360) / 45]
}

@Composable
fun TimeUpdated(time: ZonedDateTime) {
    Text(
        text = stringResource(R.string.time_updated, TimeFormatter.toDate(time)),
        style = MaterialTheme.typography.body2
    )
}

@Composable
fun Rain(rain: Double) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Image(
            painter = painterResource(R.drawable.rain_drop),
            contentDescription = stringResource(R.string.rain),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = stringResource(R.string.millimeters, "%.1f".format(rain)))
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
            contentDescription = stringResource(R.string.snow),
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(text = stringResource(R.string.millimeters, "%.1f".format(snow)))
    }
}

@Composable
fun NoDataMessage(message: String) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                text = message,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
fun StickyListHeader(text: String) {
    val bgColor = MaterialTheme.colors.secondary
    Text(
        text = text,
        color = contentColorFor(backgroundColor = bgColor),
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RectangleShape)
            .padding(horizontal = 16.dp, vertical = 4.dp),
        style = MaterialTheme.typography.subtitle1
    )
}