package com.example.lifeforge.util

import java.time.LocalDate
import java.time.ZoneId

data class DayBounds(
    val date: LocalDate,
    val startMillis: Long,
    val endMillis: Long
) {
    companion object {
        fun forDate(date: LocalDate, zoneId: ZoneId = ZoneId.systemDefault()): DayBounds {
            val start = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
            val end = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
            return DayBounds(date, start, end)
        }

        fun today(zoneId: ZoneId = ZoneId.systemDefault()): DayBounds =
            forDate(LocalDate.now(zoneId), zoneId)
    }
}
