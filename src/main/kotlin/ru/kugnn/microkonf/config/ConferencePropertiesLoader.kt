package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


@Factory
class ConferencePropertiesLoader {
    @Bean
    fun conferenceProperties(): ConferenceProperties {
        return ConferenceProperties(
                constants = "blocks/constants.yaml".readProperty(),
                blocks = "blocks/blocks.yaml".readProperty(),
                conference = "blocks/index/conference.yaml".readProperty(),
                gallery = "blocks/index/gallery.yaml".readProperty(),
                organizers = "blocks/index/organizers.yaml".readProperty(),
                partners = "blocks/index/partners.yaml".readProperty(),
                resources = "blocks/index/resources.yaml".readProperty(),
                statistics = "blocks/index/statistics.yaml".readProperty(),
                tickets = "blocks/index/tickets.yaml".readProperty(),
                venue = "blocks/index/venue.yaml".readProperty(),
                schedule = "blocks/schedule/schedule.yaml".readProperty(),
                commonSessions = "blocks/sessions/common-sessions.yaml".readProperty(),
                speakerSessions = "blocks/sessions/speaker-sessions.yaml".readProperty(),
                speakers = "blocks/speakers/speakers.yaml".readProperty(),
                teams = "blocks/team/team.yaml".readProperty()
        )
    }

    private inline fun <reified T : Any> String.readProperty(): T {
        val fileName: String = this.substringAfterLast("/").substringBeforeLast(".")

        val local: T = this.readPropertyFromFile()

        try {
            val documentSnapshot = firestore
                    .collection(FirestoreCollectionName)
                    .document(fileName)
                    .get()
                    .get(1, TimeUnit.SECONDS)

            if (documentSnapshot.exists()) {
                log.info("$fileName data: ${documentSnapshot.data}")
                return documentSnapshot.data!!.toDataClass()
            } else {
                log.info("No $fileName exists, adding from local")

                val writeResult = firestore
                        .collection(FirestoreCollectionName)
                        .document(fileName)
                        .set(local.toMap(), SetOptions.merge())
                        .get(1, TimeUnit.SECONDS)

                log.info("$fileName added at ${writeResult.updateTime}")

                return local
            }
        } catch (e: Exception) {
            log.error("Failed to load $fileName -> ${e.message}")
            log.trace(e.message, e)

            return local
        }
    }

    private inline fun <reified T> String.readPropertyFromFile(): T {
        require(!this.isBlank()) { "Resource name is not defined" }

        return ClassLoader.getSystemClassLoader().getResource(this)?.run {
            yamlMapper.readValue(this, object : TypeReference<T>() {})
        } ?: error("Cannot load $this resource")
    }

    private inline fun <reified T : Any> T.toMap(): Map<String, Any> {
        return yamlMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})
    }

    private inline fun <reified T> Map<String, Any>.toDataClass(): T {
        val json = yamlMapper.writeValueAsString(this)
        return yamlMapper.readValue(json, object : TypeReference<T>() {})
    }

    private fun Map<String, Any>.prettyPrint(): String {
        val sb = StringBuilder()
        val iter: Iterator<Map.Entry<String, Any>> = this.entries.iterator()
        while (iter.hasNext()) {
            val entry: Map.Entry<String, Any> = iter.next()
            sb.append(entry.key)
            sb.append('=').append('"')
            sb.append(entry.value)
            sb.append('"')
            if (iter.hasNext()) {
                sb.append(',').append(' ')
            }
        }
        return sb.toString()
    }

    companion object {
        private val log = LoggerFactory.getLogger(ConferencePropertiesLoader::class.java)

        private val yamlMapper: ObjectMapper = ObjectMapper(YAMLFactory())

        private const val FirestoreCredentialsFileName = "firebase-adminsdk.json"

        private const val FirestoreCollectionName = "microkonf"

        private val firestore: Firestore by lazy {
            ClassLoader.getSystemClassLoader()
                    .getResourceAsStream(FirestoreCredentialsFileName)
                    ?.run {
                        FirebaseApp.initializeApp(
                                FirebaseOptions.Builder()
                                        .setCredentials(GoogleCredentials.fromStream(this))
                                        .build()
                        )
                    }

            FirestoreClient.getFirestore()
        }
    }
}