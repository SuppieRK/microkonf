package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected

@Introspected
class Gallery {
    var enabled: Boolean = false
    lateinit var hashTag: String
    lateinit var description: String
    lateinit var galleryUrl: String
    lateinit var images: Map<String, Int>
}