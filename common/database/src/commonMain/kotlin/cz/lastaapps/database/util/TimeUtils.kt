/*
 * Copyright 2022, Petr Laštovička as Lasta apps, All rights reserved
 *
 * This file is part of ČVUT Bus.
 *
 * ČVUT Bus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ČVUT Bus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ČVUT Bus.  If not, see <https://www.gnu.org/licenses/>.
 */


package cz.lastaapps.database.util

import kotlinx.datetime.*

object TimeUtils {
    fun getDaysOfWeek() = daysOfWeekSorted
}

fun Instant.roundToSeconds(): Instant =
    Instant.fromEpochSeconds(this.epochSeconds, 0)

/** Central Europe timezone*/
val CET get() = TimeZone.of("Europe/Prague")

/** Days in Czech week */
internal val daysOfWeekSorted = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

val DayOfWeek.index: Int
    get() = daysOfWeekSorted.indexOf(this)

/** Compares a day of week to another using czech calendar
 * @return if the other day is later in week */
fun DayOfWeek.compareInWeek(other: DayOfWeek): Int {
    return daysOfWeekSorted.indexOf(this).compareTo(daysOfWeekSorted.indexOf(other))
}

/** Maps day names to day enum
 * @return DayOfWeek or null */
fun String.toCzechDayShortcutToDayOfWeek(): DayOfWeek? {
    return when (this.lowercase()) {
        "po" -> DayOfWeek.MONDAY
        "út" -> DayOfWeek.TUESDAY
        "st" -> DayOfWeek.WEDNESDAY
        "čt" -> DayOfWeek.THURSDAY
        "pá" -> DayOfWeek.FRIDAY
        "so" -> DayOfWeek.SATURDAY
        "ne" -> DayOfWeek.SUNDAY
        else -> null
    }
}

/**
 * @return the first monday before the date given, for mondays it returns the same date
 * */
internal fun LocalDate.toMonday(): LocalDate {
    var tempDate = this
    while (tempDate.dayOfWeek != DayOfWeek.MONDAY) {
        tempDate = tempDate.minus(1, DateTimeUnit.DAY)
    }
    return tempDate
}

fun kotlin.time.Duration.toHours() = inWholeHours.toInt()
fun kotlin.time.Duration.toMinutes() = (inWholeMinutes % 60).toInt()
fun kotlin.time.Duration.toSeconds() = (inWholeSeconds % 60).toInt()
