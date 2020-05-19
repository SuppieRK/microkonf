package ru.kugnn.microkonf.config.blocks

class VenueProperties {
    companion object {
        const val SOURCE = "/config/blocks/venue.yaml"
    }

    var enabled: Boolean = false
    lateinit var name: String
    lateinit var description: String
    lateinit var address: String
    lateinit var image: String
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    fun mapLink(): String {
        return "https://maps.google.com/?ll=$latitude,$longitude"
    }
}
