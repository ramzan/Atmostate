package ca.ramzan.atmostate.ui.forecast

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object TimeFormatter {
    private val dayHourFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val clockHourOfDayFormatter = DateTimeFormatter.ofPattern("ha")
    private val hourOfDayFormatter = DateTimeFormatter.ofPattern("H")
    private val weekDayFormatter = DateTimeFormatter.ofPattern("EEEE")

    fun toDayHour(time: ZonedDateTime): String = dayHourFormatter.format(time)

    fun toDate(time: ZonedDateTime): String = DateTimeFormatter.RFC_1123_DATE_TIME.format(time)

    fun toHourOfDay(time: ZonedDateTime): String = clockHourOfDayFormatter.format(time)

    fun isMidnight(time: ZonedDateTime): Boolean = hourOfDayFormatter.format(time).equals("0")

    fun toWeekDay(time: ZonedDateTime): String = weekDayFormatter.format(time)
}