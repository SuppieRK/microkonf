package ru.kugnn.microkonf.config

import ru.kugnn.microkonf.config.blocks.*
import ru.kugnn.microkonf.Utils.lazyLoader
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Singleton
class SiteProperties {
    companion object {
        // Date objects
        val DateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        const val ConstantsKey = "constants"
        const val ConferencePropertiesKey = "conference"
        const val PreviousStatisticsKey = "previousStatistics"
        const val OrganizerPropertiesKey = "organizers"
        const val TicketPropertiesKey = "tickets"
        const val GalleryPropertiesKey = "gallery"
        const val PartnerPropertiesKey = "partners"
        const val EventResourcePropertiesKey = "resources"
        const val VenuePropertiesKey = "venue"
    }

    val constants: SiteConstants by lazy { SiteConstants() }

    val conferenceProperties: ConferenceProperties by lazyLoader(ConferenceProperties::class.java, ConferenceProperties.SOURCE)
    val previousStatistics: PreviousStatistics by lazyLoader(PreviousStatistics::class.java, PreviousStatistics.SOURCE)
    val organizerProperties: OrganizerProperties by lazyLoader(OrganizerProperties::class.java, OrganizerProperties.SOURCE)
    val ticketProperties: TicketProperties by lazyLoader(TicketProperties::class.java, TicketProperties.SOURCE)
    val galleryProperties: GalleryProperties by lazyLoader(GalleryProperties::class.java, GalleryProperties.SOURCE)
    val partnerProperties: PartnerProperties by lazyLoader(PartnerProperties::class.java, PartnerProperties.SOURCE)
    val eventResourceProperties: EventResourceProperties by lazyLoader(EventResourceProperties::class.java, EventResourceProperties.SOURCE)
    val venueProperties: VenueProperties by lazyLoader(VenueProperties::class.java, VenueProperties.SOURCE)

    fun asMap(): Map<String, Any> {
        return mapOf(
                ConstantsKey to constants,
                ConferencePropertiesKey to conferenceProperties,
                PreviousStatisticsKey to previousStatistics,
                OrganizerPropertiesKey to organizerProperties,
                TicketPropertiesKey to ticketProperties,
                GalleryPropertiesKey to galleryProperties,
                PartnerPropertiesKey to partnerProperties.partners,
                EventResourcePropertiesKey to eventResourceProperties.resources,
                VenuePropertiesKey to venueProperties
        )
    }
}
