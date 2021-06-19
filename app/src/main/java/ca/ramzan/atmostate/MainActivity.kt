package ca.ramzan.atmostate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import ca.ramzan.atmostate.network.WeatherRepository
import ca.ramzan.atmostate.network.WeatherResult
import ca.ramzan.atmostate.ui.theme.AtmostateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
        lifecycleScope.launchWhenStarted {
            val lat = 33.44
            val lon = -94.04
            val res = WeatherRepository.getForecast(lat, lon)
            setContent {
                App(
                    content = when (res) {
                        is WeatherResult.Success -> res.toString()
                        is WeatherResult.Failure -> res.error
                    }
                )
            }
        }
    }
}

@Composable
fun App(content: String = "Loading") {
    AtmostateTheme {
        Text(
            text = content,
            modifier = Modifier.verticalScroll(rememberScrollState())
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App()
}
