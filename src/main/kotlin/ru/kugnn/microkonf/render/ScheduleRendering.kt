package ru.kugnn.microkonf.render

import com.neovisionaries.i18n.LanguageAlpha3Code
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.Utils.durationString
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSession
import ru.kugnn.microkonf.render.CommonRenderingUtils.buildShortSpeakerRow
import ru.kugnn.microkonf.render.CommonRenderingUtils.rawHtml
import ru.kugnn.microkonf.structures.ScheduleStructure
import java.time.LocalTime

object ScheduleRendering {
    fun ScheduleStructure.renderScheduleHeader() = createHTML().div(classes = "scheduleNavBar") {
        ul(classes = "container nav nav-tabs") {
            id = "scheduleNav"

            attributes["role"] = "tablist"

            days.withIndex().forEach { (index, day) ->
                li(classes = "nav-item") {
                    a(href = "#${day.id}", classes = "nav-link") {
                        id = "${day.id}tab"

                        attributes["data-toggle"] = "tab"
                        attributes["role"] = "tab"
                        attributes["aria-controls"] = day.id

                        if (index == 0) {
                            classes = classes + "active"

                            attributes["aria-selected"] = true.toString()
                        } else {
                            attributes["aria-selected"] = false.toString()
                        }

                        +day.date
                    }
                }
            }
        }
    }

    fun ScheduleStructure.renderSchedule() = createHTML().div(classes = "container px-4 px-lg-0 pb-4") {
        id = "schedule"

        div(classes = "tab-content") {
            id = "scheduleNavContent"

            days.withIndex().forEach { (index, day) ->
                div(classes = "tab-pane fade") {
                    if (index == 0) {
                        classes = classes + setOf("show", "active")
                    }

                    id = day.id

                    attributes["role"] = "tabpanel"
                    attributes["aria-labelledby"] = "${day.id}tab"

                    div(classes = "grid") {
                        style = "--tracks-number: ${day.tracks.size};"

                        day.timeslots.forEach { timeslot ->
                            buildStartTimeCell(timeslot.index, timeslot.startsAt)

                            timeslot.sessions.forEach { (cellId, cell) ->
                                div(classes = "session") {
                                    style = cell.gridArea.toStyleString()

                                    val session = sessions[cellId]
                                    val duration = with(timeslot) { durationString(startsAt, endsAt) }

                                    when (session) {
                                        is SpeakerSession -> buildSpeakerSessionCardBody(this@renderSchedule, cell.tracks[0], duration, session)
                                        is CommonSession -> buildCommonSessionCardBody(duration, session)
                                        else -> buildPlaceholderSessionCardBody()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.buildSpeakerSessionCardBody(schedule: ScheduleStructure, track: String, duration: String, speakerSession: SpeakerSession) {
        buildCardWrapper {
            div(classes = "card-header sessionHeader") {
                div(classes = "clearfix") {
                    h5(classes = "sessionTitle") {
                        +speakerSession.title
                    }
                    speakerSession.language?.apply {
                        p(classes = "sessionLanguage text-muted") {
                            +LanguageAlpha3Code.findByName(this@apply)[0].alpha2.name.toUpperCase()
                        }
                    }
                }
            }

            div(classes = "card-text") {
                p(classes = "sessionText") {
                    +speakerSession.description
                }
            }

            div(classes = "card-footer mt-auto sessionFooter") {
                p(classes = "text-muted") {
                    style = "margin-bottom: 0.2rem;"

                    +"$duration • $track"
                }
                speakerSession.complexity?.apply {
                    p(classes = "sessionComplexity text-muted") {
                        +this@apply
                    }
                }

                schedule.ifHasSpeakers(speakerSession) { speakers ->
                    speakers.forEach { speaker ->
                        buildShortSpeakerRow(speaker)
                    }
                }
            }

            a(classes = "stretched-link") {
                href = "#modal${speakerSession.id}"
                attributes["data-toggle"] = "modal"
            }
        }
    }

    private fun FlowContent.buildCommonSessionCardBody(duration: String, commonSession: CommonSession) {
        buildCardWrapper {
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
                    CommonSessionSvgs.Icons[this]
                }?.apply {
                    div(classes = "commonSessionIcon") {
                        rawHtml {
                            this@apply
                        }
                    }
                }
            }
        }
    }

    private fun FlowContent.buildPlaceholderSessionCardBody() {
        buildCardWrapper {
            h5(classes = "card-title card-header sessionHeader") {
                +"To be discussed"
            }
        }
    }

    private fun FlowContent.buildStartTimeCell(rowIndex: Int, timeslotStartsAt: LocalTime) {
        div(classes = "startTime") {
            style = CssGridAreaProperty(
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

    private fun FlowContent.buildCardWrapper(block: DIV.() -> Unit = {}) {
        div(classes = "card h-100") {
            style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

            div(classes = "card-body d-flex flex-column") {
                block.invoke(this)
            }
        }
    }
}