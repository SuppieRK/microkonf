package ru.kugnn.microkonf.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import org.slf4j.LoggerFactory
import ru.kugnn.microkonf.config.ConferenceProperties

@Controller
class SiteController(
        private val mapper: ObjectMapper,
        private val conferenceProperties: ConferenceProperties
) {
    companion object {
        private val log = LoggerFactory.getLogger(SiteController::class.java)
    }

    @Get
    @View("index")
    fun index(): ConferenceProperties {
        log.error(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(conferenceProperties))
        return conferenceProperties
    }
}