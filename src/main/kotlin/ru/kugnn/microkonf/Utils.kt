package ru.kugnn.microkonf

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {
    val TicketDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")

    val ParseDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val DisplayDateFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, YYYY")

    val DayFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
    val MonthDormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
    val YearFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")

    fun getSortedDateBounds(startDate: String, endDate: String, formatter: DateTimeFormatter): Pair<LocalDateTime, LocalDateTime> {
        var start = LocalDate.parse(startDate, formatter).atStartOfDay()
        var end = LocalDate.parse(endDate, formatter).atStartOfDay()

        if (start.isAfter(end)) {
            start = end.also { end = start }
        }

        return Pair(start, end)
    }
}
