package ca.ramzan.atmostate.database.weather

import androidx.room.Entity
import androidx.room.PrimaryKey
import ca.ramzan.atmostate.domain.Alert
import ca.ramzan.atmostate.domain.Current
import ca.ramzan.atmostate.domain.Daily
import ca.ramzan.atmostate.domain.Hourly
import ca.ramzan.atmostate.ui.forecast.TimeFormatter
import java.time.Instant
import java.time.ZoneId

@Entity(tableName = "current_table")
data class DbCurrent(
    @PrimaryKey
    val cityId: Long,
    val date: Long,
    val tz: String,
    val sunrise: Long,
    val sunset: Long,
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
    val windDeg: Int,
    val icon: String,
    val description: String
)

@Entity(tableName = "hourly_table", primaryKeys = ["cityId", "date"])
data class DbHourly(
    val cityId: Long,
    val date: Long,
    val tz: String,
    val temp: Int,
    val feelsLike: Int,
    val windSpeed: Int,
    val windGust: Int,
    val windDeg: Int,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String
)

@Entity(tableName = "daily_table", primaryKeys = ["cityId", "date"])
data class DbDaily(
    val cityId: Long,
    val date: Long,
    val tz: String,
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
    val windDeg: Int,
    val pop: Int,
    val rain: Double?,
    val snow: Double?,
    val icon: String,
    val description: String
)

@Entity(tableName = "alert_table", primaryKeys = ["cityId", "alertId"])
data class DbAlert(
    val cityId: Long,
    val alertId: Long,
    val tz: String,
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)

val directions by lazy { listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW") }

fun degreeToDirection(deg: Int): String {
    return directions[(deg % 360) / 45]
}

fun DbCurrent.asDomainModel(): Current {
    return Current(
        lastUpdated = Instant.ofEpochSecond(date).atZone(ZoneId.of(tz)),
        sunrise = Instant.ofEpochSecond(sunrise).atZone(ZoneId.of(tz)),
        sunset = Instant.ofEpochSecond(sunset).atZone(ZoneId.of(tz)),
        temp,
        feelsLike,
        pressure,
        humidity,
        dewPoint,
        clouds,
        uvi,
        visibility,
        windSpeed,
        windGust,
        degreeToDirection(windDeg),
        icon,
        description
    )
}

fun List<DbHourly>.asDomainModel(): List<Hourly> {
    return map { hourly ->
        hourly.run {
            val time = Instant.ofEpochSecond(date).atZone(ZoneId.of(tz))
            Hourly(
                time,
                temp,
                feelsLike,
                windSpeed,
                windGust,
                degreeToDirection(windDeg),
                pop,
                rain,
                snow,
                icon,
                description,
                TimeFormatter.timeToAlpha(time)
            )
        }
    }
}

@JvmName("asDomainModelDbDaily")
fun List<DbDaily>.asDomainModel(): List<Daily> {
    return map { daily ->
        daily.run {
            Daily(
                date = Instant.ofEpochSecond(date).atZone(ZoneId.of(tz)),
                tempMin,
                tempMax,
                tempMorn,
                tempDay,
                tempEve,
                tempNight,
                feelsLikeMorn,
                feelsLikeDay,
                feelsLikeEve,
                feelsLikeNight,
                humidity,
                windSpeed,
                windGust,
                degreeToDirection(windDeg),
                pop,
                rain,
                snow,
                icon,
                description
            )
        }
    }
}

@JvmName("asDomainModelDbAlert")
fun List<DbAlert>.asDomainModel(): List<Alert> {
    return map { alert ->
        alert.run {
            Alert(
                senderName = senderName,
                event = event,
                start = Instant.ofEpochSecond(start).atZone(ZoneId.of(tz)),
                end = Instant.ofEpochSecond(end).atZone(ZoneId.of(tz)),
                description = description
            )
        }
    }
}