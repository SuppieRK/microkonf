package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.core.io.ResourceLoader
import org.slf4j.LoggerFactory
import ru.kugnn.microkonf.config.blocks.Gallery
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Collectors

@Factory
class ConferencePropertiesLoader(
        private val resourceLoader: ResourceLoader
) {
    @Bean
    fun conferenceProperties(): ConferenceProperties {
        return getConferenceFromYaml()
    }

    private fun getConferenceFromYaml(): ConferenceProperties {
        val conferenceProperties = ConferenceProperties()

        val availableBlocks = IndexTemplatesFolder.readResourcesFromDirectory().map {
            File(it.toURI()).nameWithoutExtension
        }.toSet()

        val blocksMap = BlocksFolder.readResourcesFromDirectory().map {
            File(it.toURI()).nameWithoutExtension to (BlocksFolder + it.toString().substringAfterLast(BlocksFolder))
        }.toMap()

        conferenceProperties.blocks = availableBlocks.intersect(blocksMap.keys)

        blocksMap.forEach { (block, blockFile) ->
            when (block) {
                "conference" -> conferenceProperties.conference = blockFile.readResourceValue()
                "gallery" -> {
                    conferenceProperties.gallery = blockFile.readResourceValue<Gallery>().apply {
                        val foundImages = "public/images/gallery".readResourcesFromDirectory()
                                .map { "public" + it.path.substringAfter("public") }
                                .mapIndexed { index, path -> path to index }
                                .toMap()

                        require(foundImages.size <= 10) {
                            "Please, use no more than 10 images in your gallery, because more than 10 images breaks gallery view for iPhone 5 users."
                        }

                        this.images = foundImages
                    }
                }
                "organizers" -> conferenceProperties.organizers = blockFile.readResourceValue()
                "partners" -> conferenceProperties.partners = blockFile.readResourceValues()
                "resources" -> conferenceProperties.resources = blockFile.readResourceValues()
                "statistics" -> conferenceProperties.statistics = blockFile.readResourceValue()
                "tickets" -> conferenceProperties.tickets = blockFile.readResourceValue()
                "venue" -> conferenceProperties.venue = blockFile.readResourceValue()
                else -> log.warn("Unknown resource: '$blockFile'")
            }
        }

        return conferenceProperties
    }

    private inline fun <reified T> String.readResourceValue(): T {
        val typeReference = object : TypeReference<T>() {}

        var result: T? = null

        resourceLoader.getResource(this).ifPresentOrElse({
            result = yamlMapper.readValue(it, typeReference)
        }, {
            result = null
        })

        return result ?: error("Cannot load $this resource")
    }

    private inline fun <reified T> String.readResourceValues(): List<T> {
        val javaType = yamlMapper.typeFactory.constructType(object : TypeReference<T>() {})
        val collectionType = yamlMapper.typeFactory.constructCollectionType(List::class.java, javaType)

        var result: List<T>? = null

        resourceLoader.getResource(this).ifPresentOrElse({
            result = yamlMapper.readValue(it, collectionType)
        }, {
            result = null
        })

        return result ?: error("Cannot load $this resource")
    }

    private fun String.readResourcesFromDirectory(): List<URL> {
        val result: MutableList<URL> = mutableListOf()
        resourceLoader.getResources(this).collect(Collectors.toList()).forEach { directoryUrl ->
            Files.list(Path.of(directoryUrl.toURI())).forEach {
                result.add(it.toUri().toURL())
            }
        }
        return result
    }

    companion object {
        val log = LoggerFactory.getLogger(ConferencePropertiesLoader::class.java)

        private const val BlocksFolder = "blocks"
        private const val IndexTemplatesFolder = "views/blocks/index"

        private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
    }
}