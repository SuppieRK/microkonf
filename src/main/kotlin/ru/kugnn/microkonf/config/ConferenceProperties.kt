package ru.kugnn.microkonf.config

import com.neovisionaries.i18n.LanguageAlpha3Code
import io.micronaut.core.annotation.Introspected
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessionSvg
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.team.Team
import ru.kugnn.microkonf.config.style.Constants
import ru.kugnn.microkonf.render.GridArea

@Introspected
data class ConferenceProperties(
        var page: String = "home",
        // General properties
        val constants: Constants,
        val blocks: List<String>,
        val conference: Conference,
        val gallery: Gallery,
        val organizers: Organizers,
        val partners: List<Partners>,
        val resources: List<Resource>,
        val statistics: Statistics,
        val tickets: Tickets,
        val venue: Venue,
        // Schedule
        val schedule: List<ScheduleDay>,
        // Sessions
        val commonSessions: List<CommonSession>,
        val sessions: List<Session>,
        // Speakers
        val speakers: List<Speaker>,
        // Teams
        val teams: List<Team>
) {
    // Schedule table must be code generated and not templated due to its complexity
    val scheduleTable: String by lazy {
        val sortedSchedule: Iterable<IndexedValue<ScheduleDay>> = schedule.sortedBy { it.date }.withIndex()

        createHTML().div(classes = "container px-0 pb-4") {
            id = "schedule"

            ul(classes = "nav nav-tabs") {
                id = "scheduleNav"

                attributes["role"] = "tablist"

                sortedSchedule.forEach { (index: Int, scheduleDay: ScheduleDay) ->
                    li(classes = "nav-item") {
                        a(href = "#${scheduleDay.dayId}", classes = "nav-link") {
                            id = "${scheduleDay.dayId}tab"

                            attributes["data-toggle"] = "tab"
                            attributes["role"] = "tab"
                            attributes["aria-controls"] = scheduleDay.dayId

                            if (index == 0) {
                                classes = classes + "active"

                                attributes["aria-selected"] = true.toString()
                            } else {
                                attributes["aria-selected"] = false.toString()
                            }

                            +scheduleDay.dayString
                        }
                    }
                }
            }

            div(classes = "tab-content") {
                id = "scheduleNavContent"

                sortedSchedule.forEach { (index: Int, scheduleDay: ScheduleDay) ->
                    div(classes = "tab-pane fade") {
                        id = scheduleDay.dayId

                        attributes["role"] = "tabpanel"
                        attributes["aria-labelledby"] = "${scheduleDay.dayId}tab"

                        if (index == 0) classes = classes + setOf("show", "active")

                        buildScheduleTableUsingCssGrid(scheduleDay)
                    }
                }
            }
        }
    }

    private fun FlowContent.buildScheduleTableUsingCssGrid(scheduleDay: ScheduleDay) {
        val tracksAmount: Int = scheduleDay.tracks.size

        // Track number -> Desired colspan, decreasing with each row
        val tracksSpanTracker: HashMap<Int, Int> = (1..tracksAmount).map { it to 0 }.toMap(HashMap())

        div(classes = "grid") {
            style = "--tracks-number: ${tracksAmount};"

            (1..scheduleDay.timeslots.size).zip(scheduleDay.timeslots).forEach { (rowIndex, timeslot) ->
                buildStartTimeCell(rowIndex, timeslot)

                val sessionsInSlot: Int = timeslot.sessions.size
                val spannedColumns: Int = tracksSpanTracker.count { (_, remainingColSpans) ->
                    remainingColSpans != 0
                }

                timeslot.sessions.withIndex().forEach { (index, session) ->
                    // Array indices start from zero, however, CSS grid indices start from 1
                    val columnIndex: Int = index + 1

                    // Slot spans indicate that we need to take current slot + N next slots
                    // Here N is the value set by user, thus we need to increase the value by 1
                    tracksSpanTracker[columnIndex] = session.slotSpan?.run { this + 1 } ?: 0

                    div(classes = "session") {
                        style = GridArea(
                                rowStart = rowIndex,
                                columnStart = columnIndex,
                                rowEnd = rowIndex + 1 + (session.slotSpan ?: 0), // End values must be +1 from start
                                columnEnd = columnIndex + if ((sessionsInSlot + spannedColumns) != tracksAmount) {
                                    tracksAmount - spannedColumns
                                } else {
                                    1 // End values must be +1 from start
                                }
                        ).toStyleString()

                        buildSession(scheduleDay.tracks[index], timeslot, session)
                    }
                }

                tracksSpanTracker.forEach { (trackIndex, _) ->
                    tracksSpanTracker[trackIndex] = tracksSpanTracker[trackIndex]?.run {
                        if (this > 0) this - 1 else this
                    } ?: 0
                }
            }
        }
    }

    private fun FlowContent.buildStartTimeCell(rowIndex: Int, timeslot: ScheduleDay.Timeslot) {
        div(classes = "startTime") {
            style = GridArea(
                    rowStart = rowIndex,
                    columnStart = 1
            ).toStyleString()

            span(classes = "timeslotHours") {
                text(timeslot.startsAt.hour)
            }
            span(classes = "timeslotMinutes") {
                timeslot.startsAt.minute.apply {
                    text(if (this == 0) "00" else this.toString())
                }
            }
        }
    }

    private fun FlowContent.buildSession(track: String, timeslot: ScheduleDay.Timeslot, session: ScheduleDay.SessionCell) {
        val speakerSession: Session? = sessions.find { it.title == session.title }
        val commonSession: CommonSession? = commonSessions.find { it.title == session.title }

        div(classes = "card h-100") {
            style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

            div(classes = "card-body d-flex flex-column") {
                when {
                    speakerSession != null -> buildSpeakerSessionCardBody(track, timeslot, speakerSession)
                    commonSession != null -> buildCommonSessionCardBody(timeslot, commonSession)
                    else -> buildPlaceholderSessionCardBody()
                }
            }
        }
    }

    private fun FlowContent.buildSpeakerSessionCardBody(track: String, timeslot: ScheduleDay.Timeslot, session: Session) {
        div(classes = "card-header sessionHeader") {
            div(classes = "clearfix") {
                h5(classes = "sessionTitle") {
                    +session.title
                }
                session.language?.apply {
                    p(classes = "sessionLanguage text-muted") {
                        +LanguageAlpha3Code.findByName(this@apply)[0].alpha2.name.toUpperCase()
                    }
                }
            }
        }

        div(classes = "card-text") {
            p(classes = "sessionText") {
                +session.description
            }
        }

        div(classes = "card-footer mt-auto sessionFooter") {
            p(classes = "text-muted") {
                style = "margin-bottom: 0.2rem;"

                +"${timeslot.durationString()} • $track"
            }
            session.complexity?.apply {
                p(classes = "sessionComplexity text-muted") {
                    +this@apply
                }
            }

            session.speakers?.mapNotNull { speakerName ->
                speakers.find { speakerName == it.name }
            }?.forEach { speaker ->
                div(classes = "sessionSpeaker row") {
                    img(classes = "col sessionSpeakerImg lazyloaded") {
                        alt = speaker.name
                        src = speaker.photo
                    }
                    div(classes = "col align-self-center") {
                        p(classes = "sessionSpeakerName") {
                            +speaker.name
                        }
                        p(classes = "sessionSpeakerDescription") {
                            +"${if (speaker.company != null) "${speaker.company.name} • " else ""}${speaker.country}"
                        }
                    }
                }
            }
        }

        a(classes = "stretched-link") {
            href = "#modal${session.id}"
            attributes["data-toggle"] = "modal"
        }
    }

    private fun FlowContent.buildCommonSessionCardBody(timeslot: ScheduleDay.Timeslot, commonSession: CommonSession) {
        div(classes = "card-header mt-auto sessionHeader") {
            h5 {
                +commonSession.title
            }
        }

        div(classes = "sessionContent") {
            commonSession.description?.apply {
                p(classes = "sessionText") {
                    +this@apply
                }
            }
        }

        div(classes = "card-footer sessionFooter") {
            p(classes = "text-muted") {
                style = "margin-bottom: 0;"

                +timeslot.durationString()
            }

            commonSession.icon?.run {
                CommonSessionSvg.Icons[this]
            }?.apply {
                div(classes = "commonSessionIcon") {
                    rawHtml {
                        this@apply
                    }
                }
            }
        }
    }

    private fun FlowContent.buildPlaceholderSessionCardBody() {
        h5(classes = "card-title card-header sessionHeader") {
            +"To be discussed"
        }
    }

    private fun FlowContent.rawHtml(html: () -> String) {
        consumer.onTagContentUnsafe {
            +html.invoke()
        }
    }
}