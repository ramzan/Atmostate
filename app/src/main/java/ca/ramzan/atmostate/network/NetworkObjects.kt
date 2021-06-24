package ca.ramzan.atmostate.network

import com.squareup.moshi.Json

sealed class WeatherResult {
    data class Success(
        val lat: Double,
        val lon: Double,
        val timezone: String,
        val timezone_offset: Long,
        val current: Current,
        val hourly: List<Hourly>,
        val daily: List<Daily>,
        val alerts: List<Alert>,
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

data class Current(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
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

data class Hourly(
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

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    val moonset: Long,
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

data class Alert(
    @Json(name = "sender_name") val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)