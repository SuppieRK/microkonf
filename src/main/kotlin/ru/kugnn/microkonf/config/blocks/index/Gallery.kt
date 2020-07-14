package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Gallery @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean,
        @JsonProperty("hashTag") var hashTag: String,
        @JsonProperty("description") var description: String,
        @JsonProperty("url") var url: String,
        @JsonProperty("localImages") @get:JsonIgnore var localImages: List<String>
) {
    @get:JsonIgnore
    val images: Map<String, Int> = {
        val foundImages: Map<String, Int> = localImages.mapIndexed { index, path -> path to index }.toMap()

        require(foundImages.size <= 10) {
            "Please, use no more than 10 images in your gallery, " +
                    "because more than 10 images breaks gallery view for iPhone 5 users."
        }

        foundImages
    }.invoke()
}