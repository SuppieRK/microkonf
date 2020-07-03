package ru.kugnn.microkonf.render

import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.render.CommonRenderers.buildShortSpeakerRow

object ModalsRenderer {
    fun renderSpeakerSessionModals(schedule: Schedule, speakers: Speakers, speakerSessions: SpeakerSessions) = createHTML().div {
        id = "speakerSessionModals"

        schedule.days.forEach { scheduleDay ->
            scheduleDay.timeslots.forEach { timeslot ->
                timeslot.sessions.withIndex().map { (index, sessionCell) ->
                    index to speakerSessions.sessions.find { it.title == sessionCell.title }
                }.filter { (_, session) ->
                    session != null
                }.forEach { (_, session) ->
                    buildModalBaseFrame(
                            modalId = session!!.id,
                            modalTitle = session.title
                    ) {
                        div(classes = "row mx-auto mt-4") {
                            p {
                                +"${scheduleDay.dayString}, ${timeslot.startsAt} - ${timeslot.endsAt}"
                            }

                            session.complexity?.apply {
                                p {
                                    +"Content level: ${this@apply}"
                                }
                            }

                            p {
                                +session.description
                            }
                        }

                        session.speakers?.mapNotNull { speakerName ->
                            speakers.speakers.find { speakerName == it.name }
                        }?.apply {
                            p {
                                +"Speakers"
                            }

                            this.forEach { speaker ->
                                buildShortSpeakerRow(speaker, withModalToggle = true)
                            }
                        }
                    }
                }
            }
        }
    }

    fun renderSpeakerModals(speakers: Speakers, speakerSessions: SpeakerSessions) = createHTML().div {
        id = "speakerModals"

        speakers.speakers.forEach { speaker ->
            buildModalBaseFrame(
                    modalId = speaker.id,
                    modalTitle = speaker.name
            ) {
                div(classes = "row") {
                    div(classes = "col-12 col-lg-2") {
                        img(classes = "speakerImg lazyload") {
                            style = "margin-top: 0 !important"
                            alt = speaker.name
                            attributes["data-src"] = speaker.photo
                        }
                    }
                    div(classes = "col-12 col-lg-9 mt-4 ml-0 mt-lg-0 ml-lg-4 align-self-center") {
                        h6 {
                            +speaker.country
                        }
                        speaker.pronouns?.apply {
                            p {
                                +"Pronouns as $this"
                            }
                        }
                        p {
                            +"${speaker.jobTitle}${speaker.company?.run { " • ${this.name}" } ?: ""}"
                        }
                    }
                }

                div(classes = "row mx-auto mt-4") {
                    p {
                        +speaker.bio
                    }
                }

                speaker.socials?.apply {
                    div(classes = "row mx-auto") {
                        ul(classes = "nav justify-content-center") {
                            this@apply.forEach { social ->
                                li(classes = "nav-item") {
                                    a(classes = "nav-link teamSocial") {
                                        style = "font-size: 20px !important; line-height: 20px !important;"
                                        target = "_blank"
                                        rel = "noopener noreferrer"
                                        href = social.url

                                        i(classes = "fa fa-${social.type} mr-sm-1")
                                    }
                                }
                            }
                        }
                    }
                }

                speakerSessions.sessions.find { session ->
                    session.speakers?.any { speakerName -> speakerName == speaker.name } ?: false
                }?.apply {
                    div(classes = "row mx-auto") {
                        div(classes = "row") {
                            val session = this@apply

                            style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

                            p {
                                +session.title
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

    private fun FlowContent.buildModalBaseFrame(modalId: String, modalTitle: String, block: DIV.() -> Unit = {}) {
        div(classes = "modal") {
            id = "modal$modalId"

            attributes["tabindex"] = "-1"
            attributes["role"] = "dialog"
            attributes["aria-labelledby"] = "modal${modalId}Label"
            attributes["aria-hidden"] = "true"

            div(classes = "modal-dialog modal-dialog-centered modal-lg") {
                div(classes = "modal-content") {
                    div(classes = "modal-header") {
                        h5(classes = "modal-title mb-1") {
                            +modalTitle
                        }
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
                    div(classes = "modal-body") {
                        div(classes = "container-fluid") {
                            block.invoke(this)
                        }
                    }
                }
            }
        }
    }
}