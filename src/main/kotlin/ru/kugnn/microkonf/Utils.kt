package ru.kugnn.microkonf

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Utils {
    const val ParseDateFormat = "dd-MM-yyyy"

    val LocalTimeFormat: DateTimeFormatter = makeFormatter("HH:mm")

    val TicketDateFormat: DateTimeFormatter = makeFormatter("MMM d")

    val DisplayDateFormat: DateTimeFormatter = makeFormatter("MMMM d, YYYY")

    val DayFormat: DateTimeFormatter = makeFormatter("d")
    val MonthFormat: DateTimeFormatter = makeFormatter("MMMM")
    val YearFormat: DateTimeFormatter = makeFormatter("YYYY")

    fun getSortedDateBounds(startDate: LocalDate, endDate: LocalDate): Pair<LocalDate, LocalDate> {
        return if (startDate.isAfter(endDate)) {
            Pair(endDate, startDate)
        } else {
            Pair(startDate, endDate)
        }
    }

    private fun makeFormatter(pattern: String) = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"))
}
