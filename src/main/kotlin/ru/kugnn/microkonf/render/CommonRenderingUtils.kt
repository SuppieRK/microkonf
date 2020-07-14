package ru.kugnn.microkonf.render

import kotlinx.html.*
import ru.kugnn.microkonf.config.blocks.speakers.Speaker

object CommonRenderingUtils {
    fun FlowContent.renderSpeakerInfoRow(speaker: Speaker, withModalToggle: Boolean = false) {
        div(classes = "sessionSpeaker row") {
            if (withModalToggle) {
                style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

                a(classes = "stretched-link") {
                    href = "#modal${speaker.id}"
                    attributes["data-toggle"] = "modal"
                    attributes["data-dismiss"] = "modal"
                }
            }

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

    fun FlowContent.rawHtml(html: () -> String) {
        consumer.onTagContentUnsafe {
            +html.invoke()
        }
    }
}