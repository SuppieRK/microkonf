package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class PartnerSection @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("items") var items: List<PartnerInfo>
) {
    @Introspected
    data class PartnerInfo @JsonCreator constructor(
            @JsonProperty("name") var name: String,
            @JsonProperty("url") var url: String,
            @JsonProperty("logo") var logo: String
    )
}