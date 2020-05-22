package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Organizers @JsonCreator constructor(
        @JsonProperty("description") var description: String ,
        @JsonProperty("userGroups") var userGroups: List<Organizer>
) {
    @Introspected
    data class Organizer @JsonCreator constructor(
            @JsonProperty("shortName") var shortName: String,
            @JsonProperty("fullName") var fullName: String,
            @JsonProperty("logo") var logo: String,
            @JsonProperty("photo") var photo: String,
            @JsonProperty("email") var email: String? = null,
            @JsonProperty("description") var description: String,
            @JsonProperty("organizationUrl") var organizationUrl: String,
            @JsonProperty("links") var links: List<OrganizerSocials>
    )

    @Introspected
    data class OrganizerSocials @JsonCreator constructor(
            @JsonProperty("type") var type: String,
            @JsonProperty("url") var url: String,
            @JsonProperty("featured") var featured: Boolean = false
    )
}