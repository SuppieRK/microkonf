package ru.kugnn.microkonf.structures

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.schedule.Timeslot
import ru.kugnn.microkonf.config.blocks.sessions.GenericSession
import ru.kugnn.microkonf.render.CssGridAreaProperty
import java.time.LocalDate
import kotlin.math.min

@Introspected
data class ScheduleDayStructure(
        val id: String,
        val date: String,
        val tracks: List<String>,
        val timeslots: List<TimeslotStructure>
) {
    companion object {
        fun create(day: ScheduleDay, sessions: Map<String, GenericSession>): ScheduleDayStructure {
            val localDate = LocalDate.parse(day.dayDate, Utils.ParseDateFormatter)
            val date = Utils.ScheduleDateFormat.format(localDate)
            val id = date.replace(" ", "").toLowerCase()

            val timeslotSessions: Map<String, SessionCellStructure> = day.createSessionAreas(sessions)
            val timeslots = day.timeslots.mapIndexed { index, timeslot ->
                timeslot.run {
                    TimeslotStructure(
                            index = index + 1,
                            startsAt = startsAt,
                            endsAt = endsAt,
                            sessions = timeslotSessions.filter { (_, session) ->
                                this.sessions.any { timeslotSession ->
                                    timeslotSession.title == session.title
                                }
                            }
                    )
                }
            }

            return ScheduleDayStructure(
                    id = id,
                    date = date,
                    tracks = day.tracks,
                    timeslots = timeslots
            )
        }

        private fun ScheduleDay.createSessionAreas(sessions: Map<String, GenericSession>): Map<String, SessionCellStructure> {
            val matrix = createSessionsMatrix(tracks, timeslots)

            val visitedSessions = mutableSetOf<String>()

            val sessionAreas = mutableMapOf<String, CssGridAreaProperty>()

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
                                CssGridAreaProperty(
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
                val tracks = ((gridArea.columnStart - 1) until min(gridArea.columnEnd!!, tracks.size)).map {
                    tracks[it]
                }

                val entries = sessions.filterValues { session ->
                    session.title == sessionTitle
                }.mapValues { (_, session) ->
                    SessionCellStructure(session.id, session.title, tracks, gridArea)
                }

                val session = if (entries.isEmpty()) {
                    SessionCellStructure(sessionTitle, tracks, gridArea)
                } else {
                    entries.iterator().next().value
                }

                session.id to session
            }.toMap()
        }

        private fun createSessionsMatrix(tracks: List<String>, timeslots: List<Timeslot>): List<MutableList<String?>> {
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
    }
}