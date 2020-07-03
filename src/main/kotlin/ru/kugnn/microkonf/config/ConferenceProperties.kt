package ru.kugnn.microkonf.config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.neovisionaries.i18n.LanguageAlpha3Code
import io.micronaut.core.annotation.Introspected
import kotlinx.html.*
import kotlinx.html.stream.createHTML
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDto
import ru.kugnn.microkonf.config.blocks.schedule.TimeslotDescription
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessions
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.config.blocks.team.Teams
import ru.kugnn.microkonf.config.style.Blocks
import ru.kugnn.microkonf.config.style.Constants
import ru.kugnn.microkonf.render.CommonSessionSvg
import ru.kugnn.microkonf.render.GridArea
import java.time.LocalTime

@Introspected
data class ConferenceProperties(
        @get:JsonIgnore var page: String = "home",
        // General properties
        val constants: Constants,
        val blocks: Blocks,
        val conference: Conference,
        val gallery: Gallery,
        val organizers: Organizers,
        val partners: Partners,
        val resources: Resources,
        val statistics: Statistics,
        val tickets: Tickets,
        val venue: Venue,
        // Schedule
        val schedule: Schedule,
        // Sessions
        val commonSessions: CommonSessions,
        val speakerSessions: SpeakerSessions,
        // Speakers
        val speakers: Speakers,
        // Teams
        val teams: Teams
) {
    fun toDto(): ConferencePropertiesDto {
        return ConferencePropertiesDto(
                constants,
                blocks,
                conference.toDto(),
                gallery,
                organizers,
                partners,
                resources,
                statistics,
                tickets.toDto(),
                venue,
                schedule.toDto(),
                commonSessions,
                speakerSessions,
                speakers,
                teams
        )
    }

    companion object {
        fun fromDto(dto: ConferencePropertiesDto): ConferenceProperties {
            return ConferenceProperties(
                    constants = dto.constants,
                    blocks = dto.blocks,
                    conference = Conference.fromDto(dto.conference),
                    gallery = dto.gallery,
                    organizers = dto.organizers,
                    partners = dto.partners,
                    resources = dto.resources,
                    statistics = dto.statistics,
                    tickets = Tickets.fromDto(dto.tickets),
                    venue = dto.venue,
                    schedule = Schedule.fromDto(dto.schedule),
                    commonSessions = dto.commonSessions,
                    speakerSessions = dto.sessions,
                    speakers = dto.speakers,
                    teams = dto.teams
            )
        }
    }

    private val scheduleData: Map<ScheduleDay, List<TimeslotDescription>> by lazy {
        schedule.days.map { day -> day to day.timeslotDescriptions }.sortedBy { it.first.date }.toMap()
    }

    // Modal windows creation
    @get:JsonIgnore
    val speakerSessionModals: String by lazy {
        createHTML().div {
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
    }

    @get:JsonIgnore
    val speakerModals: String by lazy {
        createHTML().div {
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
    }

    // Schedule table must be code generated and not templated due to its complexity
    @get:JsonIgnore
    val scheduleTable: String by lazy {
        val sortedSchedule = scheduleData.entries.withIndex()

        createHTML().div(classes = "container px-0 pb-4") {
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

                                        buildSession(sessionDescription.tracks, sessionDescription.title, description.period.durationString())
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

    private fun FlowContent.buildSession(tracks: List<String>, sessionTitle: String, duration: String) {
        val speakerSession: Session? = speakerSessions.sessions.find { it.title == sessionTitle }
        val commonSession: CommonSession? = commonSessions.sessions.find { it.title == sessionTitle }

        div(classes = "card h-100") {
            style = "transform: rotate(0);" // Prevent stretched link to go beyond this DIV (for safety reasons)

            div(classes = "card-body d-flex flex-column") {
                when {
                    // TODO document this behavior later (we pick only first track for speaker session from the list)
                    speakerSession != null -> buildSpeakerSessionCardBody(tracks[0], duration, speakerSession)
                    commonSession != null -> buildCommonSessionCardBody(duration, commonSession)
                    else -> buildPlaceholderSessionCardBody()
                }
            }
        }
    }

    private fun FlowContent.buildSpeakerSessionCardBody(track: String, duration: String, session: Session) {
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

    private fun FlowContent.buildShortSpeakerRow(speaker: Speaker, withModalToggle: Boolean = false) {
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

    private fun FlowContent.rawHtml(html: () -> String) {
        consumer.onTagContentUnsafe {
            +html.invoke()
        }
    }
}

@Introspected
data class ConferencePropertiesDto(
        // General properties
        val constants: Constants,
        val blocks: Blocks,
        val conference: ConferenceDto,
        val gallery: Gallery,
        val organizers: Organizers,
        val partners: Partners,
        val resources: Resources,
        val statistics: Statistics,
        val tickets: TicketsDto,
        val venue: Venue,
        // Schedule
        val schedule: ScheduleDto,
        // Sessions
        val commonSessions: CommonSessions,
        val sessions: SpeakerSessions,
        // Speakers
        val speakers: Speakers,
        // Teams
        val teams: Teams
)