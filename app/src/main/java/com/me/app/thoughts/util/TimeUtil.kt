package com.me.app.thoughts.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * @author cwj
 * @since 2023/9/7
 */
object TimeUtil {
    private val SIMPLE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private val MONTH_DAY_FORMAT = DateTimeFormatter.ofPattern("MM-dd")
    private val HOUR_MINUTE_FORMAT = DateTimeFormatter.ofPattern("HH:mm")

    /**
     * 将带时区的时间转为系统时间
     *
     * @param time 带时区的时间
     * @return 系统本地时间
     */
    fun parse(time: ZonedDateTime): LocalDateTime =
        time.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()

    fun parse(timestamp: Long): LocalDateTime {
        val instant = Instant.ofEpochMilli(timestamp)
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
    }

    fun parseDateTime(time: String): LocalDateTime = LocalDateTime.parse(time, SIMPLE_FORMAT)

    fun format(timestamp: Long): String = SIMPLE_FORMAT.format(parse(timestamp))

    fun formatMonthDay(date: LocalDate): String = MONTH_DAY_FORMAT.format(date)
    fun formatHourMinute(time: LocalTime): String = HOUR_MINUTE_FORMAT.format(time)
    fun timestamp(hour: Int, minute: Int = 0, second: Int = 0) =
        LocalDate.now().atTime(hour, minute, second).atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()
}
