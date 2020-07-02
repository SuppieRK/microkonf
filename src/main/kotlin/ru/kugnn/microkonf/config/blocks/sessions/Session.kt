package ru.kugnn.microkonf.config.blocks.sessions

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.generateHash

@Introspected
data class Session @JsonCreator constructor(
        @JsonProperty("title") val title: String,
        @JsonProperty("description") val description: String,
        @JsonProperty("complexity") val complexity: String?,
        @JsonProperty("language") val language: String?,
        @JsonProperty("tags") val tags: List<String>?,
        @JsonProperty("speakers") val speakers: List<String>?,
        @JsonProperty("resources") val resources: Resources?
) {
    @get:JsonIgnore
    val id: String by lazy {
        generateHash(title + hashCode())
    }

    @Introspected
    data class Resources @JsonCreator constructor(
            @JsonProperty("presentation") val presentation: String?,
            @JsonProperty("video") val video: String?
    )
}