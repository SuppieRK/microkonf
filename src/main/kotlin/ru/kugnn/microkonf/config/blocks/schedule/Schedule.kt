package ru.kugnn.microkonf.config.blocks.schedule

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.Utils.parsePeriod
import java.time.LocalDate
import java.time.LocalTime

@Introspected
data class Schedule @JsonCreator constructor(
        @JsonProperty("days") val days: List<ScheduleDay>
)

@Introspected
data class ScheduleDay @JsonCreator constructor(
        @JsonProperty("date") val dayDate: String,
        @JsonProperty("tracks") val tracks: List<String>,
        @JsonProperty("timeslots") val timeslots: List<Timeslot>
) {
    @get:JsonIgnore
    val date: LocalDate = LocalDate.parse(dayDate, Utils.ParseDateFormatter)
}

@Introspected
data class Timeslot @JsonCreator constructor(
        @JsonProperty("period") private val period: String,
        @JsonProperty("sessions") val sessions: List<SessionCell>
) {
    @get:JsonIgnore
    val startsAt: LocalTime = parsePeriod(period).first

    @get:JsonIgnore
    val endsAt: LocalTime = parsePeriod(period).second
}

@Introspected
data class SessionCell @JsonCreator constructor(
        @JsonProperty("title") val title: String,
        @JsonProperty("slotSpan") val slotSpan: Int?,
        @JsonProperty("tracksSpan") val tracksSpan: Int?
)