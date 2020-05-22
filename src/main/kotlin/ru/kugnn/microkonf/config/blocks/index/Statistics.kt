package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Statistics @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean = true,
        @JsonProperty("items") var items: List<PreviousStatisticsItem>
) {
    @Introspected
    data class PreviousStatisticsItem @JsonCreator constructor(
            @JsonProperty("number") var number: Int,
            @JsonProperty("label") var label: String
    )
}