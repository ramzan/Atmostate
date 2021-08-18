package ca.ramzan.atmostate.network

import ca.ramzan.atmostate.database.weather.DbAlert
import ca.ramzan.atmostate.database.weather.DbCurrent
import ca.ramzan.atmostate.database.weather.DbDaily
import ca.ramzan.atmostate.database.weather.DbHourly
import com.squareup.moshi.Json
import java.util.*
import kotlin.math.roundToInt

sealed class WeatherResult {
    data class Success(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Long,
        val current: NetworkCurrent,
        val hourly: List<NetworkHourly>,
        val daily: List<NetworkDaily>,
        val alerts: List<NetworkAlert>?,
    ) : WeatherResult()

    data class Failure(
        val error: String
    ) : WeatherResult()
}


data class Precipitation(
    @Json(name = "1h") val hour: Double
)

data class Weather(
    val id: String,
    val main: String,
    val description: String,
    val icon: String,
)

// dt properties are Unix seconds
data class NetworkCurrent(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    val clouds: Double,
    val uvi: Double,
    val visibility: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val rain: Precipitation?,
    val snow: Precipitation?,
    val weather: List<Weather>
)

data class NetworkHourly(
    val dt: Long,
    val temp: Double,
    @Json(name = "feels_like") val feelsLike: Double,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    val clouds: Double,
    val uvi: Double,
    val visibility: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val pop: Double,
    val rain: Precipitation?,
    val snow: Precipitation?,
    val weather: List<Weather>
)

data class NetworkDaily(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val moonrise: Long?,
    val moonset: Long?,
    @Json(name = "moon_phase") val moonPhase: Double,
    val temp: Temp,
    @Json(name = "feels_like") val feelsLike: FeelsLike?,
    val pressure: Double,
    val humidity: Double,
    @Json(name = "dew_point") val dewPoint: Double,
    @Json(name = "wind_speed") val windSpeed: Double,
    @Json(name = "wind_gust") val windGust: Double?,
    @Json(name = "wind_deg") val windDeg: Int,
    val uvi: Double,
    val pop: Double,
    val rain: Double?,
    val snow: Double?,
    val weather: List<Weather>
) {
    data class Temp(
        val morn: Double,
        val day: Double,
        val eve: Double,
        val night: Double,
        val min: Double,
        val max: Double,
    )

    data class FeelsLike(
        val morn: Double,
        val day: Double,
        val eve: Double,
        val night: Double,
    )
}

data class NetworkAlert(
    @Json(name = "sender_name") val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)

fun NetworkCurrent.asDatabaseModel(cityId: Long, tz: String): DbCurrent {
    return DbCurrent(
        cityId = cityId,
        date = dt,
        tz = tz,
        sunrise = sunrise ?: 0,
        sunset = sunset ?: 0,
        temp = temp.roundToInt(),
        feelsLike = feelsLike.roundToInt(),
        pressure = pressure / 10,
        humidity = humidity.roundToInt(),
        dewPoint = dewPoint.roundToInt(),
        clouds = clouds.roundToInt(),
        uvi = uvi.roundToInt(),
        visibility = (visibility / 1000).roundToInt(),
        windSpeed = (windSpeed * 3.6).roundToInt(),
        windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
        windDeg = windDeg,
        icon = weather.first().icon,
        description = weather.first().description.capitalized(),
    )
}

fun List<NetworkHourly>.asDatabaseModel(cityId: Long, tz: String): List<DbHourly> {
    return map { hourly ->
        hourly.run {
            DbHourly(
                cityId = cityId,
                date = dt,
                tz = tz,
                temp = temp.roundToInt(),
                feelsLike = feelsLike.roundToInt(),
                windSpeed = (windSpeed * 3.6).roundToInt(),
                windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                windDeg = windDeg,
                pop = (pop * 100).roundToInt(),
                rain = rain?.hour,
                snow = snow?.hour,
                icon = weather.first().icon,
                description = weather.first().description.capitalized(),
            )
        }
    }
}

@JvmName("asDatabaseModelNetworkDaily")
fun List<NetworkDaily>.asDatabaseModel(cityId: Long, tz: String): List<DbDaily> {
    return map { daily ->
        daily.run {
            DbDaily(
                cityId = cityId,
                date = dt,
                tz = tz,
                tempMin = temp.min.roundToInt(),
                tempMax = temp.max.roundToInt(),
                tempMorn = temp.morn.roundToInt(),
                tempDay = temp.day.roundToInt(),
                tempEve = temp.eve.roundToInt(),
                tempNight = temp.night.roundToInt(),
                feelsLikeMorn = feelsLike?.morn?.roundToInt(),
                feelsLikeDay = feelsLike?.day?.roundToInt(),
                feelsLikeEve = feelsLike?.eve?.roundToInt(),
                feelsLikeNight = feelsLike?.night?.roundToInt(),
                humidity = humidity.roundToInt(),
                windSpeed = (windSpeed * 3.6).roundToInt(),
                windGust = ((windGust ?: 0.0) * 3.6).roundToInt(),
                windDeg = windDeg,
                pop = (pop * 100).roundToInt(),
                rain = rain,
                snow = snow,
                icon = weather.first().icon,
                description = weather.first().description.capitalized(),
            )
        }
    }
}

@JvmName("asDatabaseModelNetworkAlert")
fun List<NetworkAlert>?.asDatabaseModel(cityId: Long, tz: String): List<DbAlert> {
    return this?.mapIndexed { i, alert ->
        alert.run {
            DbAlert(
                cityId = cityId,
                alertId = i.toLong(),
                tz = tz,
                senderName = senderName,
                event = event,
                start = start,
                end = end,
                description = description,
            )
        }
    } ?: emptyList()
}

fun String.capitalized(): String {
    return this.replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(
            Locale.getDefault()
        ) else it.toString()
    }
}