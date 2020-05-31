package ru.kugnn.microkonf.config.style

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.markdownToHtml

@Introspected
data class Constants @JsonCreator constructor(
        @JsonProperty("event") val event: Event,
        @JsonProperty("social") val social: Social,
        @JsonProperty("organizers") val organizers: Organizers,
        @JsonProperty("tickets") val tickets: Tickets,
        @JsonProperty("gallery") val gallery: Gallery,
        @JsonProperty("partners") val partners: Partners,
        @JsonProperty("venue") val venue: Venue,
        @JsonProperty("speakers") val speakers: Speakers,
        @JsonProperty("team") val team: Team,
        @JsonProperty("readMore") val readMore: String,
        @JsonProperty("copyright") val copyright: String
) {
    @Introspected
    data class Event @JsonCreator constructor(
            @JsonProperty("descriptionTitle") val descriptionTitle: String
    )

    @Introspected
    data class Social @JsonCreator constructor(
            @JsonProperty("subscriptionTitle") val subscriptionTitle: String
    )

    @Introspected
    data class Organizers @JsonCreator constructor(
            @JsonProperty("singleTitle") val singleTitle: String,
            @JsonProperty("pluralTitle") val pluralTitle: String
    )

    @Introspected
    data class Tickets @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("notification") val notification: String,
            @JsonProperty("buy") val buy: String,
            @JsonProperty("get") val get: String,
            @JsonProperty("sold") val sold: String,
            @JsonProperty("upcoming") val upcoming: String
    )

    @Introspected
    data class Gallery @JsonCreator constructor(
            @JsonProperty("fullCollection") val fullCollection: String
    )

    @Introspected
    data class Partners @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("join") val join: String
    )

    @Introspected
    data class Venue @JsonCreator constructor(
            @JsonProperty("title") val title: String
    )

    @Introspected
    data class Speakers @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("description") val unescapedDescription: String
    ) {
        val description: String by lazy {
            markdownToHtml(unescapedDescription)
        }
    }

    @Introspected
    data class Team @JsonCreator constructor(
            @JsonProperty("title") val title: String,
            @JsonProperty("description") val unescapedDescription: String
    ) {
        val description: String by lazy {
            markdownToHtml(unescapedDescription)
        }
    }
}
