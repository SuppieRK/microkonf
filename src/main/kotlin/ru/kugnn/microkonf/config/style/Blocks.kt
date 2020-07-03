package ru.kugnn.microkonf.config.style

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Blocks @JsonCreator constructor(
        @JsonProperty("blocks") var blocks: List<String>
)