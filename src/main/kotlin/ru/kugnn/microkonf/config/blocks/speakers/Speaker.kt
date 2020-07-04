package ru.kugnn.microkonf.config.blocks.speakers

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.generateHash
import ru.kugnn.microkonf.config.blocks.Social

@Introspected
data class Speakers @JsonCreator constructor(
        @JsonProperty("speakers") val speakers: List<Speaker>
)

@Introspected
data class Speaker @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("pronouns") val pronouns: String?,
        @JsonProperty("photo") val photo: String,
        @JsonProperty("bio") val bio: String,
        @JsonProperty("country") val country: String,
        @JsonProperty("jobTitle") val jobTitle: String,
        @JsonProperty("featured") val featured: Boolean?,
        @JsonProperty("company") val company: Company?,
        @JsonProperty("socials") val socials: List<Social>?
) {
    @get:JsonIgnore
    val id: String by lazy {
        generateHash(name + hashCode())
    }
}

@Introspected
data class Company @JsonCreator constructor(
        @JsonProperty("name") val name: String,
        @JsonProperty("url") val url: String,
        @JsonProperty("logo") val logo: String
)