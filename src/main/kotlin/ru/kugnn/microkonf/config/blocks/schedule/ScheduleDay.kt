package ru.kugnn.microkonf.config.blocks.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.LocalTimeFormat
import ru.kugnn.microkonf.Utils.ParseDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.regex.Pattern

@Introspected
data class ScheduleDay @JsonCreator constructor(
        @JsonProperty("date") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(pattern = ParseDateFormat) val date: LocalDate,
        @JsonProperty("tracks") val tracks: List<String>,
        @JsonProperty("timeslots") val timeslots: List<Timeslot>
) {
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

        companion object {
            private val PeriodExclusionRegex: Regex = Pattern.compile("[^0-9:-]").toRegex()
        }
    }

    @Introspected
    data class SessionCell @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("extendsDown") val extendsDown: Int?
    )
}