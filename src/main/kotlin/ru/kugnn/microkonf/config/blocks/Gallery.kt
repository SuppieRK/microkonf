package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected

@Introspected
data class Gallery @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean,
        @JsonProperty("hashTag") var hashTag: String,
        @JsonProperty("description") var description: String,
        @JsonProperty("galleryUrl") var galleryUrl: String,
        @JsonProperty("localImages") var localImages: List<String>
) {
    val images: Map<String, Int> by lazy {
        val foundImages: Map<String, Int> = localImages.mapIndexed { index, path -> path to index }.toMap()

        require(foundImages.size <= 10) {
            "Please, use no more than 10 images in your gallery, " +
                    "because more than 10 images breaks gallery view for iPhone 5 users."
        }

        foundImages
    }
}