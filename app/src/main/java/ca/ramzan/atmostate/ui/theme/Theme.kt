package ca.ramzan.atmostate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Indigo700,
    primaryVariant = Indigo900,
    secondary = LightBlue300,
    error = Red300,
    background = Indigo500,
    surface = Indigo500,
    onPrimary = Color.White,
)

private val LightColorPalette = lightColors(
    primary = Orange500,
    primaryVariant = Orange700,
    secondary = Lime200,
    error = Red200,
    background = Orange100,
    surface = Orange100,

    /* Other default colors to override
background = Color.White,
surface = Color.White,
onPrimary = Color.White,
onSecondary = Color.Black,
onBackground = Color.Black,
onSurface = Color.Black,
*/
)

@Composable
fun AtmostateTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}