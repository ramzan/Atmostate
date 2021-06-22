package ca.ramzan.atmostate.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.ramzan.atmostate.MainState
import ca.ramzan.atmostate.MainViewModel
import ca.ramzan.atmostate.ui.theme.AtmostateTheme
import ca.ramzan.atmostate.ui.theme.Orange100
import ca.ramzan.atmostate.ui.theme.Orange500

@Composable
fun AtmostateApp(vm: MainViewModel = viewModel()) {
    val state = vm.state.collectAsState()
    AtmostateTheme {
        Scaffold(
            topBar = { MainAppBar() },
            drawerContent = { Text(text = "drawerContent") },
            content = {
                when (val s = state.value) {
                    is MainState.Error -> CenteredItem {
                        Text(text = s.error, textAlign = TextAlign.Center)
                    }
                    is MainState.Loaded -> CurrentForecast(data = s.data)
                    is MainState.Loading -> CenteredItem { CircularProgressIndicator() }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    AtmostateApp()
}