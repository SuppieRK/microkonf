package ru.kugnn.microkonf.config.blocks.sessions

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class SpeakerSessions @JsonCreator constructor(
        @JsonProperty("sessions") val sessions: List<SpeakerSession>
)

@Introspected
data class SpeakerSession @JsonCreator constructor(
        @JsonProperty("title") override val title: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("complexity") val complexity: String?,
        @JsonProperty("language") val language: String?,
        @JsonProperty("tags") val tags: List<String>?,
        @JsonProperty("speakers") val speakers: List<String>?,
        @JsonProperty("resources") val resources: Resources?
) : GenericSession()

@Introspected
data class Resources @JsonCreator constructor(
        @JsonProperty("presentation") val presentation: String?,
        @JsonProperty("video") val video: String?
)