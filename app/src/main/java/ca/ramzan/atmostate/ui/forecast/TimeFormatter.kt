package ca.ramzan.atmostate.ui.forecast

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object TimeFormatter {
    private val dayHourFormatter =
        DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    private val clockHourOfDayFormatter =
        DateTimeFormatter.ofPattern("ha").withZone(ZoneId.systemDefault())
    private val hourOfDayFormatter =
        DateTimeFormatter.ofPattern("H").withZone(ZoneId.systemDefault())
    private val weekDayFormatter =
        DateTimeFormatter.ofPattern("EEEE").withZone(ZoneId.systemDefault())

    fun toDayHour(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            dayHourFormatter.format(this)
        }
    }

    fun toDate(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.systemDefault()).format(this)
        }
    }

    fun toHourOfDay(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            clockHourOfDayFormatter.format(this)
        }
    }

    fun isMidnight(time: Long): Boolean {
        return Instant.ofEpochSecond(time).run {
            hourOfDayFormatter.format(this) == "0"
        }
    }

    fun toWeekDay(time: Long): String {
        return Instant.ofEpochSecond(time).run {
            weekDayFormatter.format(this)
        }
    }
}