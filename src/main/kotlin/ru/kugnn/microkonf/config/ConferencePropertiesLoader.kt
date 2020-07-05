package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory

@Factory
class ConferencePropertiesLoader {
    private val yamlMapper: ObjectMapper by lazy { ObjectMapper(YAMLFactory()) }

    @Bean
    fun conferenceProperties(): ConferenceProperties = ConferenceProperties(
            constants = "blocks/constants.yaml".readPropertyFromFile(),
            blocks = "blocks/page-blocks-order.yaml".readPropertyFromFile(),
            conference = "blocks/index/conference.yaml".readPropertyFromFile(),
            gallery = "blocks/index/gallery.yaml".readPropertyFromFile(),
            organizers = "blocks/index/organizers.yaml".readPropertyFromFile(),
            partners = "blocks/index/partners.yaml".readPropertyFromFile(),
            resources = "blocks/index/resources.yaml".readPropertyFromFile(),
            statistics = "blocks/index/statistics.yaml".readPropertyFromFile(),
            tickets = "blocks/index/tickets.yaml".readPropertyFromFile(),
            venue = "blocks/index/venue.yaml".readPropertyFromFile(),
            schedule = "blocks/schedule/schedule.yaml".readPropertyFromFile(),
            commonSessions = "blocks/sessions/common-sessions.yaml".readPropertyFromFile(),
            speakerSessions = "blocks/sessions/speaker-sessions.yaml".readPropertyFromFile(),
            speakers = "blocks/speakers/speakers.yaml".readPropertyFromFile(),
            teams = "blocks/team/team.yaml".readPropertyFromFile()
    )

    private inline fun <reified T> String.readPropertyFromFile(): T {
        require(!this.isBlank()) { "Resource name is not defined" }

        return ClassLoader.getSystemClassLoader().getResource(this)?.run {
            yamlMapper.readValue(this, object : TypeReference<T>() {})
        } ?: error("Cannot load $this resource")
    }
}