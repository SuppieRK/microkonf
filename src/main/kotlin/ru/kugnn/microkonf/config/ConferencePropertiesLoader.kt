package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import org.slf4j.LoggerFactory
import java.io.File

@Factory
class ConferencePropertiesLoader(
        @Property(name = "render.blocks.path")
        private val blocksPath: String?,
        private val renderProperties: RenderProperties
) {
    private val configurationPath: String by lazy {
        if (blocksPath.isNullOrBlank()) "blocks" else blocksPath
    }

    @Bean
    fun conferenceProperties(): ConferenceProperties {
        return getConferenceFromYaml()
    }

    private fun getConferenceFromYaml(): ConferenceProperties {
        return ConferenceProperties(
                blocks = renderProperties.index,
                conference = "$configurationPath/conference.yaml".readResourceValue(),
                gallery = "$configurationPath/gallery.yaml".readResourceValue(),
                organizers = "$configurationPath/organizers.yaml".readResourceValue(),
                partners = "$configurationPath/partners.yaml".readResourceValues(),
                resources = "$configurationPath/resources.yaml".readResourceValues(),
                statistics = "$configurationPath/statistics.yaml".readResourceValue(),
                tickets = "$configurationPath/tickets.yaml".readResourceValue(),
                venue = "$configurationPath/venue.yaml".readResourceValue()
        )
    }

    private inline fun <reified T> String?.readResourceValue(): T {
        require(!this.isNullOrBlank()) {
            "Resource is empty"
        }

        val typeReference = object : TypeReference<T>() {}

        return if (configurationPath.startsWith("/")) {
            try {
                File(this).toURI().toURL()
            } catch (e: Exception) {
                log.error(e.message, e)
                null
            }
        } else {
            ClassLoader.getSystemClassLoader().getResource(this)
        }?.run {
            yamlMapper.readValue(this, typeReference)
        } ?: error("Cannot load $this resource")
    }

    private inline fun <reified T> String?.readResourceValues(): List<T> {
        require(!this.isNullOrBlank()) {
            "Resource is empty"
        }

        val javaType = yamlMapper.typeFactory.constructType(object : TypeReference<T>() {})
        val collectionType = yamlMapper.typeFactory.constructCollectionType(List::class.java, javaType)

        return if (configurationPath.startsWith("/")) {
            try {
                File(this).toURI().toURL()
            } catch (e: Exception) {
                log.error(e.message, e)
                null
            }
        } else {
            ClassLoader.getSystemClassLoader().getResource(this)
        }?.run {
            yamlMapper.readValue(this, collectionType) as List<T>
        } ?: error("Cannot load $this resource")
    }

    companion object {
        private val log = LoggerFactory.getLogger(ConferencePropertiesLoader::class.java)

        private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory())
    }
}