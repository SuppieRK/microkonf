package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected

@Introspected
class EventResource {
    lateinit var name: String
    lateinit var url: String
}