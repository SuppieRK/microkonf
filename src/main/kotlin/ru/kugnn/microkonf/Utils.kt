package ru.kugnn.microkonf

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import ru.kugnn.microkonf.config.blocks.schedule.Timeslot
import java.math.BigInteger
import java.security.MessageDigest
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern


object Utils {
    // Date formatting
    val ParseDateFormatter: DateTimeFormatter = makeFormatter("dd-MM-yyyy")

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

    fun durationString(startDate: LocalTime, endDate: LocalTime): String {
        val duration: Duration = Duration.between(startDate, endDate).abs()

        val hours: Int = duration.toHoursPart()
        val minutes: Int = duration.toMinutesPart()

        val builder: StringBuilder = StringBuilder()

        if (hours == 1) {
            builder.append(hours).append(" hour ")
        } else if (hours > 1) {
            builder.append(hours).append(" hours ")
        }

        if (minutes == 1) {
            builder.append(minutes).append(" minute")
        } else if (minutes > 1) {
            builder.append(minutes).append(" minutes")
        }

        return builder.toString()
    }

    private val PeriodExclusionRegex: Regex = Pattern.compile("[^0-9:-]").toRegex()

    fun parsePeriod(period: String): Pair<LocalTime, LocalTime> {
        val split = period.replace(PeriodExclusionRegex, "").split("-")

        require(split.size == 2) {
            "Period '$period' does not contains two time definitions"
        }

        return LocalTime.parse(split[0], LocalTimeFormat) to LocalTime.parse(split[1], LocalTimeFormat)
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

    // ID generation utils
    fun generateHash(source: String): String {
        val digest = MessageDigest.getInstance("MD5")
        val messageDigest: ByteArray = digest.digest(source.toByteArray())
        val no = BigInteger(1, messageDigest)
        var hashtext = no.toString(16)
        while (hashtext.length < 32) {
            hashtext = "0$hashtext"
        }
        return hashtext.toUpperCase()
    }
}
