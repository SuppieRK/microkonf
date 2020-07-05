package ru.kugnn.microkonf.render

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.schedule.Timeslot
import ru.kugnn.microkonf.config.blocks.sessions.Session
import kotlin.math.min

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

object TimeslotsHelper {
    fun generateTimeslots(day: ScheduleDay): List<TimeslotDescription> {
        val timeslotSessions: Set<TimeslotSession> = createSessionAreas(
                day.tracks,
                createSessionsMatrix(
                        day.tracks,
                        day.timeslots
                )
        )

        return day.timeslots.mapIndexed { index, timeslot ->
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

    private fun createSessionAreas(tracks: List<String>, matrix: List<MutableList<String?>>): Set<TimeslotSession> {
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