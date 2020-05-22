package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Resource @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("url") var url: String
)