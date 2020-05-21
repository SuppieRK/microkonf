package ru.kugnn.microkonf.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.kugnn.microkonf.config.ConferenceProperties

@Controller
class SiteController(
        private val conferenceProperties: ConferenceProperties,
        private val objectMapper: ObjectMapper
) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(SiteController::class.java)
    }

    @Get
    @View("index")
    fun index(): Any {
//        log.error(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(conferenceProperties))
        return conferenceProperties
    }
}