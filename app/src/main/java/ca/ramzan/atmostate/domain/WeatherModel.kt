package ca.ramzan.atmostate.domain

import java.time.ZonedDateTime

data class Current(
    val lastUpdated: ZonedDateTime,
    val sunrise: ZonedDateTime,
    val sunset: ZonedDateTime,
    val temp: Int,
    val feelsLike: Int,
    val pressure: Double,
    val humidity: Int,
    val dewPoint: Int,
    val clouds: Int,
    val uvi: Int,
    val visibility: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val icon: String,
    val description: String
)

data class Hourly(
    val time: ZonedDateTime,
    val temp: Int,
    val feelsLike: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String,
    val alpha: Float
)

data class Daily(
    val date: ZonedDateTime,
    val tempMin: Int,
    val tempMax: Int,
    val tempMorn: Int,
    val tempDay: Int,
    val tempEve: Int,
    val tempNight: Int,
    val feelsLikeMorn: Int?,
    val feelsLikeDay: Int?,
    val feelsLikeEve: Int?,
    val feelsLikeNight: Int?,
    val humidity: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDirection: String,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String
)

data class Alert(
    val senderName: String,
    val event: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val description: String
)