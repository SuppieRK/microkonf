package ru.kugnn.microkonf.render

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.render.CommonRenderers.buildShortSpeakerRow

object ModalsRenderer {
    fun renderSpeakerSessionModals(
            mappedSchedule: Map<ScheduleDay, List<TimeslotDescription>>,
            speakers: Speakers,
            speakerSessions: SpeakerSessions
    ) = createHTML().div {
        id = "speakerSessionModals"

        mappedSchedule.forEach { (scheduleDay, timeslotDescriptions) ->
            timeslotDescriptions.forEach { timeslotDescription ->
                timeslotDescription.sessions.forEach { timeslotSession ->
                    speakerSessions.sessions.find {
                        it.title == timeslotSession.title
                    }?.apply {
                        buildModalBaseFrame(
                                modalId = id,
                                header = {
                                    h5(classes = "modal-title mb-1") {
                                        +this@apply.title
                                    }
                                },
                                body = {
                                    div(classes = "row mx-auto") {
                                        style = "display: block"

                                        h4 {
                                            +"${scheduleDay.dayString}, ${timeslotDescription.period.startsAt} - ${timeslotDescription.period.endsAt}"
                                        }

                                        if (!complexity.isNullOrBlank()) {
                                            h6(classes = "mt-3") {
                                                +"Content level: $complexity"
                                            }
                                        }

                                        h6(classes = "mt-3") {
                                            // We pick only first track for speaker session
                                            // Table if filled from the top to the bottom, from left to right
                                            // First available track is the one that is going to be used
                                            +timeslotSession.tracks[0]
                                        }

                                        p(classes = "my-4") {
                                            +description
                                        }
                                    }

                                    this@apply.speakers?.mapNotNull { speakerName ->
                                        speakers.speakers.find { speakerName == it.name }
                                    }?.apply {
                                        h5(classes = "mb-3") {
                                            +"Speakers"
                                        }

                                        this.forEach { speaker ->
                                            buildShortSpeakerRow(speaker, withModalToggle = true)
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }

    fun renderSpeakerModals(
            mappedSchedule: Map<ScheduleDay, List<TimeslotDescription>>,
            speakers: Speakers,
            speakerSessions: SpeakerSessions
    ) = createHTML().div {
        id = "speakerModals"

        speakers.speakers.forEach { speaker ->
            buildModalBaseFrame(
                    modalId = speaker.id,
                    header = {
                        div(classes = "row px-4 text-center text-lg-left") {
                            div(classes = "col-12 col-lg-4 p-0") {
                                img(classes = "speakerImg lazyload m-0") {
                                    alt = speaker.name
                                    attributes["data-src"] = speaker.photo
                                }
                            }
                            div(classes = "col-12 col-lg-8 mt-3 mt-lg-0 align-self-center text-nowrap") {
                                h5(classes = "modal-title m-0") {
                                    +speaker.name
                                }
                                h6(classes = "m-0 mt-1") {
                                    +speaker.country
                                }
                                p(classes = "m-0 mt-1") {
                                    +"${speaker.jobTitle}${speaker.company?.run { " • ${this.name}" } ?: ""}${speaker.pronouns?.run { " • $this" } ?: ""}"
                                }
                            }
                        }
                    },
                    body = {
                        p {
                            +speaker.bio
                        }

                        speaker.socials?.apply {
                            h5(classes = "mb-3") {
                                +"Socials"
                            }

                            ul(classes = "nav") {
                                this@apply.forEach { social ->
                                    li(classes = "nav-item") {
                                        a(classes = "nav-link teamSocial") {
                                            target = "_blank"
                                            rel = "noopener noreferrer"
                                            href = social.url

                                            i(classes = "fa fa-${social.type} mr-sm-1")
                                        }
                                    }
                                }
                            }
                        }

                        val relatedSessions = speakerSessions.sessions.filter { speakerSession ->
                            speakerSession.speakers?.contains(speaker.name) ?: false
                        }

                        if (relatedSessions.isNotEmpty()) {
                            h5(classes = "mt-3") {
                                +"Sessions"
                            }

                            mappedSchedule.forEach { (scheduleDay, timeslotDescriptions) ->
                                timeslotDescriptions.map {
                                    it to it.sessions.map { timeslotSession ->
                                        // We pick only first track for speaker session
                                        // Table if filled from the top to the bottom, from left to right
                                        // First available track is the one that is going to be used
                                        timeslotSession.tracks[0] to relatedSessions.find { speakerSession ->
                                            speakerSession.title == timeslotSession.title
                                        }
                                    }
                                }.forEach { (timeslotDescription, sessions) ->
                                    sessions.forEach { (track, session) ->
                                        if (session != null) {
                                            div(classes = "row mx-0 mt-3") {
                                                style = "transform: rotate(0); display: block" // Prevent stretched link to go beyond this DIV (for safety reasons)

                                                p(classes = "sessionSpeakerName") {
                                                    +session.title
                                                }

                                                p(classes = "sessionSpeakerDescription") {
                                                    +"${scheduleDay.dayString}, ${timeslotDescription.period.startsAt} - ${timeslotDescription.period.endsAt}"
                                                }

                                                p(classes = "sessionSpeakerDescription") {
                                                    +track
                                                }

                                                a(classes = "stretched-link") {
                                                    href = "#modal${session.id}"
                                                    attributes["data-toggle"] = "modal"
                                                    attributes["data-dismiss"] = "modal"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }

    private fun FlowContent.buildModalBaseFrame(modalId: String, header: DIV.() -> Unit = {}, body: DIV.() -> Unit = {}) {
        div(classes = "modal") {
            id = "modal$modalId"

            attributes["tabindex"] = "-1"
            attributes["role"] = "dialog"
            attributes["aria-labelledby"] = "modal${modalId}Label"
            attributes["aria-hidden"] = "true"

            div(classes = "modal-dialog modal-dialog-centered modal-lg") {
                div(classes = "modal-content") {
                    div(classes = "modal-header") {
                        header.invoke(this)
                        button(classes = "close") {
                            type = ButtonType.button

                            attributes["data-dismiss"] = "modal"
                            attributes["aria-label"] = "Close"

                            span {
                                attributes["aria-hidden"] = "true"
                                +"×"
                            }
                        }
                    }
                    div(classes = "modal-body p-4") {
                        body.invoke(this)
                    }
                }
            }
        }
    }
}