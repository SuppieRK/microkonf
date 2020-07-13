package ru.kugnn.microkonf.config.blocks.sessions

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class CommonSessions @JsonCreator constructor(
        @JsonProperty("sessions") val sessions: List<CommonSession>
)

@Introspected
data class CommonSession @JsonCreator constructor(
        @JsonProperty("title") override val title: String,
        @JsonProperty("location") val location: String?,
        @JsonProperty("description") val description: String?,
        @JsonProperty("icon") val icon: String?,
        @JsonProperty("image") val image: String?
) : GenericSession()