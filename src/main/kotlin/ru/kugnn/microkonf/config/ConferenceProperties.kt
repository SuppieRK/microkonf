package ru.kugnn.microkonf.config

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.config.blocks.*
import ru.kugnn.microkonf.config.style.SiteConstants

@Introspected
class ConferenceProperties(
        val constants: SiteConstants = SiteConstants(),
        val blocks: List<String>,
        val conference: Conference,
        val gallery: Gallery,
        val organizers: Organizers,
        val partners: List<Partners>,
        val resources: List<Resource>,
        val statistics: Statistics,
        val tickets: Tickets,
        val venue: Venue
)