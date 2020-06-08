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
import ru.kugnn.microkonf.render.GridArea
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.util.regex.Pattern
import kotlin.math.min

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
            @JsonProperty("slotSpan") val slotSpan: Int?,
            @JsonProperty("tracksSpan") val tracksSpan: Int?
    )

    // Internal data structures
    val dayString: String by lazy { ScheduleDateFormat.format(date) }
    val dayId: String by lazy { dayString.replace(" ", "").toLowerCase() }

    val timeslotDescriptions: List<TimeslotDescription> by lazy {
        val timeslotSessions: Set<TimeslotSession> = createSessionAreas(createSessionsMatrix())

        timeslots.mapIndexed { index, timeslot ->
            TimeslotDescription(
                    rowIndex = index + 1,
                    period = timeslot,
                    sessions = timeslotSessions.filter { session ->
                        timeslot.sessions.any { timeslotSession ->
                            timeslotSession.title == session.title
                        }
                    }.toSet()
            )
        }
    }

    @Introspected
    data class TimeslotDescription(
            val rowIndex: Int,
            val period: Timeslot,
            val sessions: Set<TimeslotSession>
    )

    @Introspected
    data class TimeslotSession(
            val title: String,
            val tracks: List<String>,
            val gridArea: GridArea
    )

    private fun createSessionsMatrix(): List<MutableList<String?>> {
        @Suppress("USELESS_CAST")
        val matrix: List<MutableList<String?>> = (1..timeslots.size).map {
            (1..tracks.size).map {
                null as String?
            }.toMutableList()
        }

        timeslots.withIndex().forEach { (rowIndex, timeslot) ->
            timeslot.sessions.withIndex().forEach { (columnIndex, session) ->
                var columnInc = 0

                while (matrix[rowIndex][columnIndex + columnInc] != null) {
                    columnInc++
                }

                (0..(session.tracksSpan ?: 0)).forEach { columnSpan ->
                    matrix[rowIndex][columnIndex + columnInc + columnSpan] = session.title

                    session.slotSpan?.apply {
                        (1..this).forEach { rowInc ->
                            matrix[rowIndex + rowInc][columnIndex + columnInc + columnSpan] = session.title
                        }
                    }
                }
            }
        }

        matrix.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { columnIndex, columnValue ->
                var columnInc = 1

                while (columnIndex + columnInc < row.size) {
                    if (matrix[rowIndex][columnIndex + columnInc] != null) {
                        break
                    }

                    if (matrix[rowIndex][columnIndex + columnInc] == null) {
                        matrix[rowIndex][columnIndex + columnInc] = columnValue
                    }

                    columnInc++
                }
            }
        }

        return matrix
    }

    private fun createSessionAreas(matrix: List<MutableList<String?>>): Set<TimeslotSession> {
        val visitedSessions = mutableSetOf<String>()

        val sessionAreas = mutableMapOf<String, GridArea>()

        matrix.mapIndexed { rowIndex, row ->
            row.mapIndexed { columnIndex, value ->
                if (!visitedSessions.contains(value)) {
                    var rowEnd = rowIndex
                    var columnEnd = columnIndex

                    (rowIndex until matrix.size).forEach { nextRowIndex ->
                        (columnIndex until row.size).forEach { nextColumnIndex ->
                            if (matrix[nextRowIndex][nextColumnIndex] == value) {
                                rowEnd = nextRowIndex
                                columnEnd = nextColumnIndex
                            }
                        }
                    }

                    sessionAreas.putIfAbsent(
                            value!!,
                            GridArea(
                                    rowStart = rowIndex + 1,
                                    columnStart = columnIndex + 1,
                                    rowEnd = rowEnd + 2,
                                    columnEnd = columnEnd + 2
                            )
                    )
                }
            }
        }

        return sessionAreas.map { (sessionTitle, gridArea) ->
            TimeslotSession(
                    title = sessionTitle,
                    tracks = ((gridArea.columnStart - 1) until min(gridArea.columnEnd!!, tracks.size)).map { tracks[it] },
                    gridArea = gridArea
            )
        }.toSet()
    }
}