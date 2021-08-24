package ca.ramzan.atmostate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Indigo800,
    primaryVariant = Indigo500,
    secondary = Indigo700,
    error = Red300,
    background = Indigo900,
    surface = Indigo700,
    onPrimary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
    onSecondary = Color.White
)

private val LightColorPalette = lightColors(
    primary = LightBlue200,
    primaryVariant = LightBlue300,
    secondary = LightBlue200,
    error = Red200,
    background = LightBlue50,
    surface = LightBlue50,
    onPrimary = Color.White,
    onSurface = Color.Black,
    onBackground = Color.Black,
    onSecondary = Color.White
)

val Colors.hourlyBase: Color
    @Composable get() = if (isLight) LightBlue50 else Indigo500
val Colors.hourlyOverlay: Color
    @Composable get() = if (isLight) LightBlue100 else Indigo900


@Composable
fun AtmostateTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}