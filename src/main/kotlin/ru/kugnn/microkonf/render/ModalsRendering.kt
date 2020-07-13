package ru.kugnn.microkonf.render

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.render.CommonRenderingUtils.buildShortSpeakerRow
import ru.kugnn.microkonf.structures.ScheduleStructure

object ModalsRendering {
    fun ScheduleStructure.renderSpeakerSessionModals() = createHTML().div {
        id = "speakerSessionModals"

        forEachSpeakerSession { day, timeslot, cell, speakerSession ->
            buildModalBaseFrame(
                    modalId = speakerSession.id,
                    header = {
                        h5(classes = "modal-title mb-1") {
                            +speakerSession.title
                        }
                    },
                    body = {
                        div(classes = "row mx-auto") {
                            style = "display: block"

                            h4 {
                                +"${day.date}, ${timeslot.startsAt} - ${timeslot.endsAt}"
                            }

                            if (!speakerSession.complexity.isNullOrBlank()) {
                                h6(classes = "mt-3") {
                                    +"Content level: ${speakerSession.complexity}"
                                }
                            }

                            h6(classes = "mt-3") {
                                // We pick only first track for speaker session
                                // Table if filled from the top to the bottom, from left to right
                                // First available track is the one that is going to be used
                                +cell.tracks[0]
                            }

                            p(classes = "my-4") {
                                +speakerSession.description
                            }
                        }

                        if (hasSpeakers(speakerSession)) {
                            h5(classes = "mb-3") {
                                +"Speakers"
                            }

                            getSpeakers(speakerSession).forEach { speaker ->
                                buildShortSpeakerRow(speaker, withModalToggle = true)
                            }
                        }
                    }
            )
        }
    }

    fun ScheduleStructure.renderSpeakerModals() = createHTML().div {
        id = "speakerModals"

        speakers.forEach { (speakerId, speaker) ->
            buildModalBaseFrame(
                    modalId = speakerId,
                    header = {
                        div {
                            style = "min-width: 100% !important"

                            div(classes = "speakerImgWrapper p-0 d-inline-block text-center") {
                                img(classes = "speakerImg lazyload m-0") {
                                    alt = speaker.name
                                    attributes["data-src"] = speaker.photo
                                }
                            }
                            div(classes = "speakerDescriptionWrapper ml-0 ml-lg-3 mt-0 align-self-center d-inline-block align-middle text-center text-lg-left") {
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
                            div(classes = "clearfix") { }
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

                        if (hasSessions(speaker)) {
                            h5(classes = "mt-3") {
                                +"Sessions"
                            }

                            forEachSpeakerSessionOf(speaker) { day, timeslot, cell, speakerSession ->
                                div(classes = "row mx-0 mt-3") {
                                    style = "transform: rotate(0); display: block" // Prevent stretched link to go beyond this DIV (for safety reasons)

                                    p(classes = "sessionSpeakerName") {
                                        +speakerSession.title
                                    }

                                    p(classes = "sessionSpeakerDescription") {
                                        +"${day.date}, ${timeslot.startsAt} - ${timeslot.endsAt}"
                                    }

                                    p(classes = "sessionSpeakerDescription") {
                                        +cell.tracks[0]
                                    }

                                    a(classes = "stretched-link") {
                                        href = "#modal${speakerSession.id}"
                                        attributes["data-toggle"] = "modal"
                                        attributes["data-dismiss"] = "modal"
                                    }
                                }
                            }
                        }
                    }
            )
        }
    }

    private fun FlowContent.buildModalBaseFrame(modalId: String, header: DIV.() -> Unit = {}, body: DIV.() -> Unit = {}) {
        div(classes = "modal p-0") {
            id = "modal$modalId"

            attributes["tabindex"] = "-1"
            attributes["role"] = "dialog"
            attributes["aria-labelledby"] = "modal${modalId}Label"
            attributes["aria-hidden"] = "true"

            div(classes = "modal-dialog modal-dialog-centered modal-lg") {
                div(classes = "modal-content") {
                    div(classes = "modal-header p-4") {
                        header.invoke(this)
                        button(classes = "close m-0 p-0") {
                            type = ButtonType.button

                            style = "margin-left: -1.1rem !important"

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