package ru.kugnn.microkonf.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import ru.kugnn.microkonf.config.ConferenceProperties

@Controller
class SiteController(
        private val conferenceProperties: ConferenceProperties
) {
    @Get
    @View("index")
    fun index(): ConferenceProperties {
        return conferenceProperties
    }
}