package ru.kugnn.microkonf.config

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.team.Team
import ru.kugnn.microkonf.config.style.SiteConstants

@Introspected
class ConferenceProperties(
        // General properties
        val constants: SiteConstants = SiteConstants(),
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
    val scheduleItems by lazy {
        validateSchedule()
        ""
    }

    private fun validateSchedule() {
//        schedule.days.forEach { day ->
//            require(schedule.startTime != null || day.startTime != null) {
//                "Day at ${day.date} does not have start time set"
//            }
//
//            require(schedule.breakBetweenSessions != null || day.breakBetweenSessions != null) {
//                "Day at ${day.date} does not have break time set"
//            }
//
//            day.timeSlots.forEach { timeSlot ->
//                require(schedule.length != null || day.length != null ) {
//                    "Time slot ${timeSlot.name} at day ${day.date} does not have time length set"
//                }
//            }
//        }
    }
}