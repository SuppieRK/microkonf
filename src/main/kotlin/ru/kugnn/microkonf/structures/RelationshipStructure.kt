package ru.kugnn.microkonf.structures

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSession
import ru.kugnn.microkonf.config.blocks.speakers.Speaker

@Introspected
data class RelationshipStructure(
        private val sessionsToSpeakers: Map<String, Set<String>>,
        private val speakersToSessions: Map<String, Set<String>>
) {
    fun getSpeakerIds(sessionId: String): Set<String> = sessionsToSpeakers[sessionId] ?: emptySet()

    fun getSessionIds(speakerId: String): Set<String> = speakersToSessions[speakerId] ?: emptySet()

    companion object {
        fun create(speakers: Iterable<Speaker>, sessions: Iterable<SpeakerSession>): RelationshipStructure {
            val relationships = sessions.flatMap { session ->
                session.speakers?.mapNotNull { sessionSpeakerName ->
                    speakers.find { speaker ->
                        speaker.name == sessionSpeakerName
                    }
                }?.map { speaker ->
                    session.id to speaker.id
                } ?: emptyList()
            }

            return RelationshipStructure(
                    sessionsToSpeakers = relationships.groupBy(
                            keySelector = {
                                it.first
                            },
                            valueSelector = {
                                it.second
                            }
                    ),
                    speakersToSessions = relationships.groupBy(
                            keySelector = {
                                it.second
                            },
                            valueSelector = {
                                it.first
                            }
                    )
            )
        }

        private fun <T1, T2, K, V> Iterable<Pair<T1, T2>>.groupBy(
                keySelector: (Pair<T1, T2>) -> K,
                valueSelector: (Pair<T1, T2>) -> V
        ): Map<K, Set<V>> = this.groupBy {
            keySelector.invoke(it)
        }.map { (key, values) ->
            key to values.map {
                valueSelector.invoke(it)
            }.toSet()
        }.toMap()
    }
}