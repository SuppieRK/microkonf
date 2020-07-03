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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import org.slf4j.LoggerFactory

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
                    .get()

            if (documentSnapshot.exists()) {
                log.info("$fileName data: ${documentSnapshot.data}")
                return documentSnapshot.data!!.toDataClass()
            } else {
                log.info("No $fileName exists, adding from local")

                val writeResult = firestore
                        .collection(FirestoreCollectionName)
                        .document(fileName)
                        .set(local.toMap(), SetOptions.merge())
                        .get()

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

    private fun <T> T.toMap(): Map<String, Any> {
        return convert()
    }

    private inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    private inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    companion object {
        val gson by lazy { Gson() }

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