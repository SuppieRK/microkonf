package ru.kugnn.microkonf.config

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.*

@Introspected
class ConferenceProperties(
        val constants: SiteConstants = SiteConstants(),
        val blocks: List<String>,
        val conference: Conference,
        val gallery: Gallery,
        val organizers: Organizers,
        val partners: List<PartnerSection>,
        val resources: List<EventResource>,
        val statistics: Statistics,
        val tickets: Tickets,
        val venue: Venue
)