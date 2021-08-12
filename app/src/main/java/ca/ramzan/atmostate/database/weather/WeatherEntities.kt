package ca.ramzan.atmostate.database.weather

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "current_table")
data class DbCurrent(
    @PrimaryKey
    val cityId: Long,
    val date: Long,
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
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String
)