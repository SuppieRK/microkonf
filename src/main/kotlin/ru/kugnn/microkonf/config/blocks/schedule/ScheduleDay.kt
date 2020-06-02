package ru.kugnn.microkonf.config.blocks.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.LocalTimeFormat
import ru.kugnn.microkonf.Utils.ParseDateFormat
import ru.kugnn.microkonf.Utils.ScheduleDateFormat
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.regex.Pattern

@Introspected
data class ScheduleDay @JsonCreator constructor(
        @JsonProperty("date") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(pattern = ParseDateFormat) val date: LocalDate,
        @JsonProperty("tracks") val tracks: List<String>,
        @JsonProperty("timeslots") val timeslots: List<Timeslot>
) {
    val dayString: String by lazy { ScheduleDateFormat.format(date) }
    val dayId: String by lazy { dayString.replace(" ", "").toLowerCase() }

    @Introspected
    data class Timeslot @JsonCreator constructor(
            @JsonProperty("period") private val period: String,
            @JsonProperty("sessions") val sessions: List<SessionCell>
    ) {
        private val parsedPeriod: Pair<LocalTime, LocalTime> by lazy {
            val split = period.replace(PeriodExclusionRegex, "").split("-")

            require(split.size == 2) {
                "Period '$period' does not contains two time definitions"
            }

            LocalTime.parse(split[0], LocalTimeFormat) to LocalTime.parse(split[1], LocalTimeFormat)
        }

        val startsAt: LocalTime by lazy { parsedPeriod.first }
        val endsAt: LocalTime by lazy { parsedPeriod.second }

        fun durationString(): String {
            val duration: Duration = Duration.between(startsAt, endsAt).abs()

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

        companion object {
            private val PeriodExclusionRegex: Regex = Pattern.compile("[^0-9:-]").toRegex()
        }
    }

    @Introspected
    data class SessionCell @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("slotSpan") val slotSpan: Int?
    )
}