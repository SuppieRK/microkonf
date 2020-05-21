package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected

@Introspected
class Venue {
    var enabled: Boolean = false
    lateinit var name: String
    lateinit var description: String
    lateinit var address: String
    lateinit var image: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    val mapLink: String by lazy {
        "https://maps.google.com/?ll=$latitude,$longitude"
    }
}