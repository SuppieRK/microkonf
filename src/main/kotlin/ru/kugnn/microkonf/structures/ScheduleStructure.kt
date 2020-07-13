package ru.kugnn.microkonf.structures

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessions
import ru.kugnn.microkonf.config.blocks.sessions.GenericSession
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSession
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.speakers.Speakers

@Introspected
data class ScheduleStructure(
        val days: List<ScheduleDayStructure>,
        val speakers: Map<String, Speaker>,
        val sessions: Map<String, GenericSession>,
        private val relationships: RelationshipStructure
) {
    fun getSpeakers(session: GenericSession): Set<Speaker> {
        return relationships.getSpeakerIds(session.id).mapNotNull {
            speakers[it]
        }.toSet()
    }

    fun hasSpeakers(session: GenericSession): Boolean {
        return relationships.getSpeakerIds(session.id).isNotEmpty()
    }

    fun getSessions(speaker: Speaker): Set<SpeakerSession> {
        return relationships.getSessionIds(speaker.id).mapNotNull {
            sessions[it] as? SpeakerSession
        }.toSet()
    }

    fun hasSessions(speaker: Speaker): Boolean {
        return relationships.getSessionIds(speaker.id).isNotEmpty()
    }

    fun GenericSession.isRelatedTo(speaker: Speaker): Boolean {
        return relationships.getSpeakerIds(this.id).contains(speaker.id)
    }

    fun Speaker.isRelatedTo(session: GenericSession): Boolean {
        return relationships.getSessionIds(this.id).contains(session.id)
    }

    fun forEachSpeakerSession(action: (day: ScheduleDayStructure, timeslot: TimeslotStructure, cell: SessionCellStructure, speakerSession: SpeakerSession) -> Unit) {
        forEachSession { day, timeslot, cell ->
            (sessions[cell.id] as? SpeakerSession)?.apply {
                action.invoke(day, timeslot, cell, this)
            }
        }
    }

    fun forEachSpeakerSessionOf(speaker: Speaker, action: (day: ScheduleDayStructure, timeslot: TimeslotStructure, cell: SessionCellStructure, speakerSession: SpeakerSession) -> Unit) {
        forEachSession { day, timeslot, cell ->
            (sessions[cell.id] as? SpeakerSession)?.apply {
                if (speaker.isRelatedTo(this)) {
                    action.invoke(day, timeslot, cell, this)
                }
            }
        }
    }

    private fun forEachSession(action: (day: ScheduleDayStructure, timeslot: TimeslotStructure, cell: SessionCellStructure) -> Unit) {
        days.forEach { day ->
            day.timeslots.forEach { timeslot ->
                timeslot.sessions.forEach { (_, cell) ->
                    action.invoke(day, timeslot, cell)
                }
            }
        }
    }

    companion object {
        fun create(schedule: Schedule, speakers: Speakers, speakerSessions: SpeakerSessions, commonSessions: CommonSessions): ScheduleStructure {
            val speakersMap = speakers.speakers.associateBy { it.id }

            val sessionsMap = speakerSessions.sessions.associateBy { it.id } +
                    commonSessions.sessions.associateBy { it.id }

            val relationships = RelationshipStructure.create(speakers.speakers, speakerSessions.sessions)

            val days = schedule.days.map {
                ScheduleDayStructure.create(it, sessionsMap)
            }

            return ScheduleStructure(days, speakersMap, sessionsMap, relationships)
        }
    }
}