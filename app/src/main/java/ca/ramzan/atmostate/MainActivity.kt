package ca.ramzan.atmostate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import ca.ramzan.atmostate.ui.AtmostateApp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @ExperimentalPermissionsApi
    @ExperimentalCoroutinesApi
    @ExperimentalFoundationApi
    @ExperimentalAnimationApi
    @ExperimentalPagerApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AtmostateApp() }
    }
}
