package ru.kugnn.microkonf.config

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.*

@Introspected
class ConferenceProperties {
    val constants = SiteConstants()

    lateinit var blocks: Set<String>
    lateinit var conference: Conference
    lateinit var gallery: Gallery
    lateinit var organizers: Organizers
    lateinit var partners: List<PartnerSection>
    lateinit var resources: List<EventResource>
    lateinit var statistics: Statistics
    lateinit var tickets: Tickets
    lateinit var venue: Venue
}