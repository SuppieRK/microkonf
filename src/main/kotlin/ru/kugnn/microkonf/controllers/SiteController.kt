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

    @Get("/schedule")
    @View("index")
    fun schedule(): ConferenceProperties {
        return conferenceProperties.copy(
                page = "schedule",
                blocks = listOf("schedule")
        )
    }

    @Get("/speakers")
    @View("index")
    fun speakers(): ConferenceProperties {
        return conferenceProperties.copy(
                page = "speakers",
                blocks = listOf("speakers")
        )
    }

    @Get("/team")
    @View("index")
    fun team(): ConferenceProperties {
        return conferenceProperties.copy(
                page = "team",
                blocks = listOf("team")
        )
    }
}