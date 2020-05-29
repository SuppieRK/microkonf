package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Social @JsonCreator constructor(
        @JsonProperty("type") var type: String,
        @JsonProperty("url") var url: String,
        @JsonProperty("featured") var featured: Boolean = false
)