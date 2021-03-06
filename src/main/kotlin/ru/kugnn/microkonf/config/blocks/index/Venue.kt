package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Venue @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean = false,
        @JsonProperty("name") var name: String,
        @JsonProperty("description") var description: String,
        @JsonProperty("address") var address: String,
        @JsonProperty("image") var image: String,
        @JsonProperty("latitude") var latitude: Double = 0.0,
        @JsonProperty("longitude") var longitude: Double = 0.0
) {
    @get:JsonIgnore
    val mapLink: String = "https://maps.google.com/?ll=$latitude,$longitude"
}