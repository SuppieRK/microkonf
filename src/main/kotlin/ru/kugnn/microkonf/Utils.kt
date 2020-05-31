package ru.kugnn.microkonf

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object Utils {
    // Date formatting
    const val ParseDateFormat = "dd-MM-yyyy"

    val LocalTimeFormat: DateTimeFormatter = makeFormatter("HH:mm")

    val TicketDateFormat: DateTimeFormatter = makeFormatter("MMM d")

    val ScheduleDateFormat: DateTimeFormatter = makeFormatter("MMMM d")

    val DisplayDateFormat: DateTimeFormatter = makeFormatter("MMMM d, YYYY")

    val DayFormat: DateTimeFormatter = makeFormatter("d")
    val MonthFormat: DateTimeFormatter = makeFormatter("MMMM")
    val YearFormat: DateTimeFormatter = makeFormatter("YYYY")

    private fun makeFormatter(pattern: String) = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.of("UTC"))

    // Date utils
    fun getSortedDateBounds(startDate: LocalDate, endDate: LocalDate): Pair<LocalDate, LocalDate> {
        return if (startDate.isAfter(endDate)) {
            Pair(endDate, startDate)
        } else {
            Pair(startDate, endDate)
        }
    }

    // Markdown support
    private val MarkdownParser: Parser = Parser.builder().build()
    private val MarkdownHtmlRenderer: HtmlRenderer = HtmlRenderer.builder()
            .escapeHtml(false)
            .sanitizeUrls(true)
            .build()

    fun markdownToHtml(source: String): String {
        return MarkdownHtmlRenderer.render(MarkdownParser.parse(source))
    }
}
