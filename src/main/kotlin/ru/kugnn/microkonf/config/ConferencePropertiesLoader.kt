package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.cloud.NoCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import com.google.cloud.firestore.SetOptions
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Property
import org.slf4j.LoggerFactory
import ru.kugnn.microkonf.config.application.RenderProperties
import java.io.File
import java.util.concurrent.TimeUnit

@Factory
class ConferencePropertiesLoader(
        @Property(name = "render.blocks.path")
        private val blocksPath: String?,
        @Property(name = "datasources.gcp.firestore.emulator.enabled", defaultValue = "false")
        private var firestoreEmulatorEnabled: Boolean = false,
        private val renderProperties: RenderProperties
) {
    private val firestore: Firestore = FirestoreOptions.getDefaultInstance().run {
        if (firestoreEmulatorEnabled) {
            log.warn("Using Firestore emulator")
            this.toBuilder().setCredentials(NoCredentials.getInstance()).build()
        } else {
            this
        }
    }.service

    private val configurationPath: String by lazy {
        if (blocksPath.isNullOrBlank()) "blocks" else blocksPath
    }

    @Bean
    fun conferenceProperties(): ConferenceProperties = getOrCreateProperties()

    private fun getConferenceFromYaml(): ConferenceProperties {
        return ConferenceProperties(
                constants = "$configurationPath/constants.yaml".readResourceValue(),
                blocks = renderProperties.index,
                conference = "$configurationPath/index/conference.yaml".readResourceValue(),
                gallery = "$configurationPath/index/gallery.yaml".readResourceValue(),
                organizers = "$configurationPath/index/organizers.yaml".readResourceValue(),
                partners = "$configurationPath/index/partners.yaml".readResourceValues(),
                resources = "$configurationPath/index/resources.yaml".readResourceValues(),
                statistics = "$configurationPath/index/statistics.yaml".readResourceValue(),
                tickets = "$configurationPath/index/tickets.yaml".readResourceValue(),
                venue = "$configurationPath/index/venue.yaml".readResourceValue(),
                schedule = "$configurationPath/schedule/schedule.yaml".readResourceValues(),
                commonSessions = "$configurationPath/sessions/common-sessions.yaml".readResourceValues(),
                sessions = "$configurationPath/sessions/speaker-sessions.yaml".readResourceValues(),
                speakers = "$configurationPath/speakers/speakers.yaml".readResourceValues(),
                teams = "$configurationPath/team/team.yaml".readResourceValues()
        )
    }

    private fun getOrCreateProperties(): ConferenceProperties {
        val local = getConferenceFromYaml()

        try {
            val documentSnapshot = firestore
                    .collection(FirestoreCollectionName)
                    .document(FirestoreDocumentName)
                    .get()
                    .get(1, TimeUnit.SECONDS)

            if (documentSnapshot.exists()) {
                log.warn("Document data: ${documentSnapshot.data}")
                println("Document data: ${documentSnapshot.data}")
                return ConferenceProperties.fromDto(documentSnapshot.toObject(ConferencePropertiesDto::class.java)!!)
            } else {
                log.warn("No document exists, adding from local")
                println("No document exists, adding from local")

                val writeResult = firestore
                        .collection(FirestoreCollectionName)
                        .document(FirestoreDocumentName)
                        .set(local.toDto(), SetOptions.merge())
                        .get(1, TimeUnit.SECONDS)

                log.warn("Document added at ${writeResult.updateTime}")
                println("Document added at ${writeResult.updateTime}")

                return local
            }
        } catch (e: Exception) {
            log.error("Failed to read properties from Firestore: ${e.message}, falling back to local properties", e)
            println("Failed to read properties from Firestore: ${e.message}, falling back to local properties")
            return local
        }
    }

    private inline fun <reified T> String.readResourceValue(): T {
        require(!this.isBlank()) {
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

    private inline fun <reified T> String.readResourceValues(): List<T> {
        require(!this.isBlank()) {
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

        private const val FirestoreCollectionName = "microkonf"
        private const val FirestoreDocumentName = "properties"
    }
}