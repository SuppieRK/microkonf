package ru.kugnn.microkonf.config.blocks.team

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.Social

@Introspected
data class Team @JsonCreator constructor(
        @JsonProperty("title") val title: String,
        @JsonProperty("members") val members: List<Member>
) {
    @Introspected
    data class Member @JsonCreator constructor(
            @JsonProperty("name") val name: String,
            @JsonProperty("title") val title: String,
            @JsonProperty("photo") val photo: String,
            @JsonProperty("socials") val socials: List<Social>
    )
}