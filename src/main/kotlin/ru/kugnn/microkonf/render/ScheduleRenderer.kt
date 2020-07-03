package ru.kugnn.microkonf.render

import com.neovisionaries.i18n.LanguageAlpha3Code
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.Utils.durationString
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessions
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.render.CommonRenderers.buildShortSpeakerRow
import ru.kugnn.microkonf.render.CommonRenderers.rawHtml
import java.time.LocalTime

object ScheduleRenderer {
    fun renderSchedule(schedule: Schedule, speakers: Speakers, commonSessions: CommonSessions, speakerSessions: SpeakerSessions): String {
        val sortedSchedule = schedule.days.map { day ->
            day to TimeslotsHelper.generateTimeslots(day)
        }.sortedBy {
            it.first.date
        }.toMap().entries.withIndex()

        return createHTML().div(classes = "container px-0 pb-4") {
            id = "schedule"

            ul(classes = "nav nav-tabs") {
                id = "scheduleNav"

                attributes["role"] = "tablist"

                sortedSchedule.forEach { (index, entry) ->
                    li(classes = "nav-item") {
                        a(href = "#${entry.key.dayId}", classes = "nav-link") {
                            id = "${entry.key.dayId}tab"

                            attributes["data-toggle"] = "tab"
                            attributes["role"] = "tab"
                            attributes["aria-controls"] = entry.key.dayId

                            if (index == 0) {
                                classes = classes + "active"

                                attributes["aria-selected"] = true.toString()
                            } else {
                                attributes["aria-selected"] = false.toString()
                            }

                            +entry.key.dayString
                        }
                    }
                }
            }

            div(classes = "tab-content") {
                id = "scheduleNavContent"

                sortedSchedule.forEach { (index, entry) ->
                    val scheduleDay = entry.key
                    val timeslotDescriptions = entry.value

                    div(classes = "tab-pane fade") {
                        id = scheduleDay.dayId

                        attributes["role"] = "tabpanel"
                        attributes["aria-labelledby"] = "${scheduleDay.dayId}tab"

                        if (index == 0) classes = classes + setOf("show", "active")

                        div(classes = "grid") {
                            style = "--tracks-number: ${scheduleDay.tracks.size};"

                            timeslotDescriptions.forEach { description ->
                                buildStartTimeCell(description.rowIndex, description.period.startsAt)

                                description.sessions.forEach { sessionDescription ->
                                    div(classes = "session") {
                                        style = sessionDescription.gridArea.toStyleString()

                                        buildSession(
                                                speakers,
                                                commonSessions,
                                                speakerSessions,
                                                sessionDescription.tracks,
                                                sessionDescription.title,
                                                with(description.period) { durationString(startsAt, endsAt) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.buildStartTimeCell(rowIndex: Int, timeslotStartsAt: LocalTime) {
        div(classes = "startTime") {
            style = GridArea(
                    rowStart = rowIndex,
                    columnStart = 1
            ).toStyleString()

            span(classes = "timeslotHours") {
                text(timeslotStartsAt.hour)
            }
            span(classes = "timeslotMinutes") {
                timeslotStartsAt.minute.apply {
                    text(if (this == 0) "00" else this.toString())
                }
            }
        }
    }

    private fun FlowContent.buildSession(speakers: Speakers, commonSessions: CommonSessions, speakerSessions: SpeakerSessions, tracks: List<String>, sessionTitle: String, duration: String) {
        val speakerSession: Session? = speakerSessions.sessions.find { it.title == sessionTitle }
        val commonSession: CommonSession? = commonSessions.sessions.find { it.title == sessionTitle }

        div(classes = "card h-100") {
            style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

            div(classes = "card-body d-flex flex-column") {
                when {
                    // TODO document this behavior later (we pick only first track for speaker session from the list)
                    speakerSession != null -> buildSpeakerSessionCardBody(speakers, tracks[0], duration, speakerSession)
                    commonSession != null -> buildCommonSessionCardBody(duration, commonSession)
                    else -> buildPlaceholderSessionCardBody()
                }
            }
        }
    }

    private fun FlowContent.buildSpeakerSessionCardBody(speakers: Speakers, track: String, duration: String, session: Session) {
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

                +"$duration • $track"
            }
            session.complexity?.apply {
                p(classes = "sessionComplexity text-muted") {
                    +this@apply
                }
            }

            session.speakers?.mapNotNull { speakerName ->
                speakers.speakers.find { speakerName == it.name }
            }?.forEach { speaker ->
                buildShortSpeakerRow(speaker)
            }
        }

        a(classes = "stretched-link") {
            href = "#modal${session.id}"
            attributes["data-toggle"] = "modal"
        }
    }

    private fun FlowContent.buildCommonSessionCardBody(duration: String, commonSession: CommonSession) {
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

                +duration
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
}