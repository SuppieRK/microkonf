package ru.kugnn.microkonf.config.style

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.markdownToHtml

@Introspected
data class Constants @JsonCreator constructor(
        @JsonProperty("event") var event: Event,
        @JsonProperty("social") var social: Social,
        @JsonProperty("organizers") var organizers: Organizers,
        @JsonProperty("tickets") var tickets: Tickets,
        @JsonProperty("gallery") var gallery: Gallery,
        @JsonProperty("partners") var partners: Partners,
        @JsonProperty("venue") var venue: Venue,
        @JsonProperty("speakers") var speakers: Speakers,
        @JsonProperty("schedule") var schedule: Schedule,
        @JsonProperty("team") var team: Team,
        @JsonProperty("readMore") var readMore: String,
        @JsonProperty("copyright") var copyright: String
)

@Introspected
data class Event @JsonCreator constructor(
        @JsonProperty("descriptionTitle") var descriptionTitle: String
)

@Introspected
data class Social @JsonCreator constructor(
        @JsonProperty("subscriptionTitle") var subscriptionTitle: String
)

@Introspected
data class Organizers @JsonCreator constructor(
        @JsonProperty("singleTitle") var singleTitle: String,
        @JsonProperty("pluralTitle") var pluralTitle: String
)

@Introspected
data class Tickets @JsonCreator constructor(
        @JsonProperty("title") var title: String,
        @JsonProperty("notification") var notification: String,
        @JsonProperty("buy") var buy: String,
        @JsonProperty("get") var get: String,
        @JsonProperty("sold") var sold: String,
        @JsonProperty("upcoming") var upcoming: String
)

@Introspected
data class Gallery @JsonCreator constructor(
        @JsonProperty("fullCollection") var fullCollection: String
)

@Introspected
data class Partners @JsonCreator constructor(
        @JsonProperty("title") var title: String,
        @JsonProperty("join") var join: String
)

@Introspected
data class Venue @JsonCreator constructor(
        @JsonProperty("title") var title: String
)

@Introspected
data class Speakers @JsonCreator constructor(
        @JsonProperty("title") var title: String,
        @JsonProperty("description") var unescapedDescription: String
) {
    @get:JsonIgnore
    @delegate:Transient
    val description: String by lazy {
        markdownToHtml(unescapedDescription)
    }
}

@Introspected
data class Schedule @JsonCreator constructor(
        @JsonProperty("title") var title: String
)

@Introspected
data class Team @JsonCreator constructor(
        @JsonProperty("title") var title: String,
        @JsonProperty("description") var unescapedDescription: String
) {
    @get:JsonIgnore
    @delegate:Transient
    val description: String by lazy {
        markdownToHtml(unescapedDescription)
    }
}