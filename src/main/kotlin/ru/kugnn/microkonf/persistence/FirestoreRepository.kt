package ru.kugnn.microkonf.persistence

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.api.core.ApiFutureCallback
import com.google.api.core.ApiFutures
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.google.cloud.firestore.WriteResult
import com.google.common.util.concurrent.MoreExecutors
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.cloud.FirestoreClient
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.slf4j.LoggerFactory
import ru.kugnn.microkonf.config.ConferenceProperties
import ru.kugnn.microkonf.config.blocks.index.*
import ru.kugnn.microkonf.config.blocks.schedule.Schedule
import ru.kugnn.microkonf.config.blocks.schedule.ScheduleDay
import ru.kugnn.microkonf.config.blocks.sessions.CommonSession
import ru.kugnn.microkonf.config.blocks.sessions.CommonSessions
import ru.kugnn.microkonf.config.blocks.sessions.Session
import ru.kugnn.microkonf.config.blocks.sessions.SpeakerSessions
import ru.kugnn.microkonf.config.blocks.speakers.Speaker
import ru.kugnn.microkonf.config.blocks.speakers.Speakers
import ru.kugnn.microkonf.config.blocks.team.Team
import ru.kugnn.microkonf.config.blocks.team.Teams
import javax.inject.Singleton

@Singleton
class FirestoreRepository {
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

    fun readOrInsert(): ConferenceProperties = with(LocalRepository.properties) {
        ConferenceProperties(
                conference = blockingReadOrInsert(conference, FirestoreDefaultCollectionName, "conference"),
                gallery = blockingReadOrInsert(gallery, FirestoreDefaultCollectionName, "gallery"),
                statistics = blockingReadOrInsert(statistics, FirestoreDefaultCollectionName, "statistics"),
                tickets = blockingReadOrInsert(tickets, FirestoreDefaultCollectionName, "tickets"),
                venue = blockingReadOrInsert(venue, FirestoreDefaultCollectionName, "venue"),
                blocks = blockingReadOrInsert(blocks, FirestoreDefaultCollectionName, "page-blocks-order"),
                constants = blockingReadOrInsert(constants, FirestoreDefaultCollectionName, "constants"),
                organizers = blockingReadOrInsert(organizers),
                partners = blockingReadOrInsert(partners),
                resources = blockingReadOrInsert(resources),
                schedule = blockingReadOrInsert(schedule),
                commonSessions = blockingReadOrInsert(commonSessions),
                speakerSessions = blockingReadOrInsert(speakerSessions),
                speakers = blockingReadOrInsert(speakers),
                teams = blockingReadOrInsert(teams)
        )
    }

    private fun blockingReadOrInsert(defaultValue: Organizers): Organizers {
        val collection = "organizers"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Organizers(items.map {
                    it.get().get().data!!.mapToDataClass<Organizer>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.userGroups.forEach { userGroup ->
                blockingReadOrInsert(userGroup, collection, generateId(userGroup.shortName))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: Partners): Partners {
        val collection = "partners"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Partners(items.map {
                    it.get().get().data!!.mapToDataClass<Partner>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.partners.forEach { partner ->
                blockingReadOrInsert(partner, collection, generateId(partner.name))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: Resources): Resources {
        val collection = "resources"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Resources(items.map {
                    it.get().get().data!!.mapToDataClass<Resource>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.resources.forEach { resource ->
                blockingReadOrInsert(resource, collection, generateId(resource.name))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: Schedule): Schedule {
        val collection = "schedule"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Schedule(items.map {
                    it.get().get().data!!.mapToDataClass<ScheduleDay>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.days.forEach { day ->
                blockingReadOrInsert(day, collection, day.dayDate)
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: CommonSessions): CommonSessions {
        val collection = "common-sessions"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                CommonSessions(items.map {
                    it.get().get().data!!.mapToDataClass<CommonSession>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.sessions.forEach { session ->
                blockingReadOrInsert(session, collection, generateId(session.title))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: SpeakerSessions): SpeakerSessions {
        val collection = "speaker-sessions"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                SpeakerSessions(items.map {
                    it.get().get().data!!.mapToDataClass<Session>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.sessions.forEach { session ->
                blockingReadOrInsert(session, collection, generateId(session.title))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: Speakers): Speakers {
        val collection = "speakers"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Speakers(items.map {
                    it.get().get().data!!.mapToDataClass<Speaker>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.speakers.forEach { speaker ->
                blockingReadOrInsert(speaker, collection, generateId(speaker.name))
            }

            defaultValue
        }
    }

    private fun blockingReadOrInsert(defaultValue: Teams): Teams {
        val collection = "teams"

        return try {
            val items = firestore.collection(collection).listDocuments().toList()

            if (items.isEmpty()) {
                error("Firebase does not have $collection")
            } else {
                log.info("Retrieved $collection from Firebase")
                Teams(items.map {
                    it.get().get().data!!.mapToDataClass<Team>()
                })
            }
        } catch (e: Exception) {
            log.error("Failed to read collection $collection -> ${e.message}")
            log.debug(e.message, e)

            defaultValue.teams.forEach { team ->
                blockingReadOrInsert(team, collection, generateId(team.title))
            }

            defaultValue
        }
    }

    private inline fun <reified T> blockingReadOrInsert(defaultValue: T, collection: String, document: String): T {
        try {
            val documentReference = firestore.collection(collection).document(document).get().get()

            if (documentReference.exists()) {
                log.info("Retrieved $collection/$document from Firebase")
                return documentReference.data!!.mapToDataClass()
            } else {
                ApiFutures.addCallback(
                        firestore.collection(collection).document(document).set(defaultValue.dataClassToMap(), SetOptions.merge()),
                        object : ApiFutureCallback<WriteResult> {
                            override fun onFailure(t: Throwable) {
                                log.error("Failed to save data to Firebase, ${t.message}")
                                log.debug(t.message, t)
                            }

                            override fun onSuccess(result: WriteResult) {
                                log.info("Data at $collection/$document was added to Firebase at ${result.updateTime}")
                            }
                        },
                        MoreExecutors.directExecutor()
                )

                return defaultValue
            }
        } catch (e: Exception) {
            log.error("Failed to read $collection/$document -> ${e.message}")
            log.debug(e.message, e)

            return defaultValue
        }
    }

    private fun generateId(source: String): String {
        return source.toLowerCase()
                .replace(Regex("[^a-z0-9]+"), "-")
                .replace(Regex("[-]+"), "-")
    }

    private fun <T> T.dataClassToMap(): Map<String, Any> {
        return convert()
    }

    private inline fun <reified T> Map<String, Any>.mapToDataClass(): T {
        return convert()
    }

    private inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }

    companion object {
        private val gson by lazy {
            GsonBuilder()
                    .setExclusionStrategies(object : ExclusionStrategy {
                        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                            return clazz == Lazy::class.java
                        }

                        override fun shouldSkipField(f: FieldAttributes?): Boolean {
                            return f?.getAnnotation(JsonIgnore::class.java) != null
                        }
                    })
                    .create()
        }

        private val log = LoggerFactory.getLogger(FirestoreRepository::class.java)

        private const val FirestoreCredentialsFileName = "firebase-adminsdk.json"

        private const val FirestoreDefaultCollectionName = "site-settings"
    }
}