package ru.kugnn.microkonf.config

import com.neovisionaries.i18n.LanguageAlpha3Code
import io.micronaut.core.annotation.Introspected
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.team.Team
import ru.kugnn.microkonf.config.style.Constants

@Introspected
data class ConferenceProperties(
        // General properties
        val constants: Constants,
        val page: String = "index",
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

                        buildScheduleTable(scheduleDay)
                    }
                }
            }
        }
    }

    private fun DIV.buildScheduleTable(scheduleDay: ScheduleDay) {
        // TODO rework this to CSS grid area
        table(classes = "table") {
            tbody {
                val tracksAmount: Int = scheduleDay.tracks.size
                val cellWidthPercent: String = ((1.toDouble() / tracksAmount) * 100).toString()
                        .take(3)
                        .replace(Regex("[^0-9]"), "")

                // Track number -> Desired colspan, decreasing with each row
                val colspanTracker: HashMap<Int, Int> = (1..tracksAmount).map { it to 0 }.toMap(HashMap())

                scheduleDay.timeslots.forEach { timeslot ->
                    tr {
                        td {
                            span(classes = "timeslotHours") {
                                text(timeslot.startsAt.hour)
                            }
                            span(classes = "timeslotMinutes") {
                                timeslot.startsAt.minute.apply {
                                    text(if (this == 0) "00" else this.toString())
                                }
                            }

//                            p {
//                                +timeslot.startsAt.format(Utils.LocalTimeFormat)
//                            }
                        }

                        val sessionsInSlot: Int = timeslot.sessions.size

                        val spannedColumns: Int = colspanTracker.count { (_, remainingColSpans) ->
                            remainingColSpans != 0
                        }

                        timeslot.sessions.withIndex().forEach { (index, session) ->
                            td {
                                style = "width: $cellWidthPercent%"

                                // Log colspan request if any and set rowSpan
                                if (session.slotSpan != null) {
                                    // Slot spans indicate that we need to take current slot + N next slots
                                    // Here N is the value set by user
                                    // Thus we need to increase the value by 1
                                    val tdRowSpan: Int = session.slotSpan + 1
                                    colspanTracker[index + 1] = tdRowSpan
                                    rowSpan = tdRowSpan.toString()
                                }

                                // Check and set colSpan
                                if ((sessionsInSlot + spannedColumns) != tracksAmount) {
                                    colSpan = (tracksAmount - spannedColumns).toString()
                                }

                                buildSessionBody(scheduleDay.tracks[index], timeslot, session)
                            }
                        }
                    }

                    colspanTracker.forEach { (trackIndex, _) ->
                        colspanTracker[trackIndex] = colspanTracker[trackIndex]?.run {
                            if (this > 0) this - 1 else this
                        } ?: 0
                    }
                }
            }
        }
    }

    private fun TD.buildSessionBody(track: String, timeslot: ScheduleDay.Timeslot, session: ScheduleDay.SessionCell) {
        val speakerSession: Session? = sessions.find { it.title == session.title }
        val commonSession: CommonSession? = commonSessions.find { it.title == session.title }

        if (speakerSession != null) {
            buildSpeakerSessionBody(track, timeslot, speakerSession)
        } else {
            if (commonSession != null) {
                buildCommonSessionBody(timeslot, commonSession)
            } else {
                buildPlaceholderSessionBody()
            }
        }
    }

    private fun TD.buildSpeakerSessionBody(track: String, timeslot: ScheduleDay.Timeslot, session: Session) {
        div(classes = "card h-100") {
            div(classes = "card-body") {
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
                    h6 {
                        +"${timeslot.durationString()} • $track${if (!session.complexity.isNullOrBlank()) " • ${session.complexity}" else ""}"
                    }
                }
                p(classes = "sessionText") {
                    +session.description
                }
            }
        }
    }

    private fun TD.buildCommonSessionBody(timeslot: ScheduleDay.Timeslot, commonSession: CommonSession) {
        div(classes = "card h-100") {
            div(classes = "card-body") {
                div(classes = "card-header sessionHeader") {
                    h5 {
                        +commonSession.title
                    }
                    h6 {
                        +timeslot.durationString()
                    }
                }
                commonSession.description?.apply {
                    p(classes = "sessionText") {
                        +this@apply
                    }
                }
            }
        }
    }

    private fun TD.buildPlaceholderSessionBody() {
        div(classes = "card h-100") {
            div(classes = "card-body") {
                h5(classes = "card-title card-header sessionHeader") {
                    +"To be discussed"
                }
            }
        }
    }
}