package ca.ramzan.atmostate.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import ca.ramzan.atmostate.database.cities.Coord
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationManager @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
) {

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            fusedLocationClient.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Coord? {
        return fusedLocationClient.lastLocation.await()?.run {
            Coord(latitude, longitude)
        }
    }
}