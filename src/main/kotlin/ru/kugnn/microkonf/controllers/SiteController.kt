package ru.kugnn.microkonf.controllers

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.views.View
import ru.kugnn.microkonf.config.ConferenceProperties
import ru.kugnn.microkonf.config.application.CacheableResources
import ru.kugnn.microkonf.config.blocks.speakers.Speaker

@Controller
class SiteController(
        private val conferenceProperties: ConferenceProperties,
        private val cacheableResources: CacheableResources
) {
    @Get
    @View("index")
    fun index(): ConferenceProperties {
        conferenceProperties.page = "home"
        return conferenceProperties
    }

    @Get("/schedule")
    @View("index")
    fun schedule(): ConferenceProperties {
        conferenceProperties.page = "schedule"
        return conferenceProperties
    }

    @Get("/speakers")
    @View("index")
    fun speakers(): ConferenceProperties {
        conferenceProperties.page = "speakers"
        return conferenceProperties
    }

    @Get("/team")
    @View("index")
    fun team(): ConferenceProperties {
        conferenceProperties.page = "team"
        return conferenceProperties
    }

    @Get("/speakers/{speakerName}")
    @View("index")
    fun speaker(@PathVariable("speakerName") speakerName: String): Speaker? {
        return conferenceProperties.speakers.find {
            it.name == speakerName
        }
    }

    @Get("/cache")
    fun urlsToCache(): Set<String> {
        return cacheableResources.list
    }
}