package ru.kugnn.microkonf.config

import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessions
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.config.blocks.team.Teams
import ru.kugnn.microkonf.config.style.Blocks
import ru.kugnn.microkonf.config.style.Constants
import ru.kugnn.microkonf.render.ModalsRenderer
import ru.kugnn.microkonf.render.ScheduleRenderer

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
    // Modal windows creation
    @get:JsonIgnore
    @delegate:Transient
    val speakerModals: String by lazy {
        ModalsRenderer.renderSpeakerModals(speakers, speakerSessions)
    }

    @get:JsonIgnore
    @delegate:Transient
    val speakerSessionModals: String by lazy {
        ModalsRenderer.renderSpeakerSessionModals(schedule, speakers, speakerSessions)
    }

    // Schedule table must be code generated and not templated due to its complexity
    @get:JsonIgnore
    @delegate:Transient
    val scheduleTable: String by lazy {
        ScheduleRenderer.renderSchedule(schedule, speakers, commonSessions, speakerSessions)
    }
}