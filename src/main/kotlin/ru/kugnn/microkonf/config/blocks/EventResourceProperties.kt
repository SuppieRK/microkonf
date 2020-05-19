package ru.kugnn.microkonf.config.blocks

class EventResourceProperties {
    companion object {
        const val SOURCE = "/config/blocks/event-resources.yaml"
    }

    var resources: List<EventResource> = ArrayList()
}

class EventResource {
    lateinit var name: String
    lateinit var url: String
}