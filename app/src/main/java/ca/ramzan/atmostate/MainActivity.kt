package ca.ramzan.atmostate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import ca.ramzan.atmostate.network.WeatherResult
import ca.ramzan.atmostate.ui.theme.AtmostateTheme
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect {
                setContent { App(state = it) }
            }
        }
    }
}

@Composable
fun App(state: MainState) {
    AtmostateTheme {
        Scaffold(
            topBar = { MainAppBar() },
            drawerContent = { Text(text = "drawerContent") },
            content = {
                when (state) {
                    is MainState.Error -> CenteredItem {
                        Text(
                            text = state.error,
                            textAlign = TextAlign.Center
                        )
                    }
                    is MainState.Loaded -> CurrentForecast(data = state.data)
                    is MainState.Loading -> CenteredItem {
                        CircularProgressIndicator()
                    }
                }
            },
            backgroundColor = Orange100,
        )
    }
}

@Composable
fun MainAppBar() {
    TopAppBar(
        title = { Text("TopAppBar") },
        backgroundColor = Orange500,
        navigationIcon = {
            IconButton(onClick = {}) {
                Icon(Icons.Filled.Menu, contentDescription = "Menu")
            }
        }
    )
}

@Composable
fun CenteredItem(content: @Composable () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) { content() }
}

@Composable
fun CurrentForecast(data: WeatherResult.Success) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        item {
            LocationInfo(
                lat = data.lat,
                lon = data.lon,
                tz = data.timezone,
                tzOffset = data.timezone_offset
            )
        }
    }
}

@Composable
fun LocationInfo(lat: Double, lon: Double, tz: String, tzOffset: Long) {
    Column {
        Text(text = "Location", style = TextStyle(fontWeight = FontWeight.Bold))
        Text(text = "Coordinates: $lat, $lon")
        Text(text = "Time zone: $tz")
        Text(text = "Time zone offset: $tzOffset")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    App(MainState.Loading)
}
