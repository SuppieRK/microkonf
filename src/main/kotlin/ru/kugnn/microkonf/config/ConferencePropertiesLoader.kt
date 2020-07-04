package ru.kugnn.microkonf.config

import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import ru.kugnn.microkonf.persistence.FirestoreRepository

@Factory
class ConferencePropertiesLoader(
        private val firestoreRepository: FirestoreRepository
) {
    @Bean
    fun conferenceProperties(): ConferenceProperties = firestoreRepository.readOrInsert()
}