package ru.kugnn.microkonf.config

import io.micronaut.core.annotation.Introspected
import java.time.format.DateTimeFormatter

@Introspected
class SiteConstants {
    companion object {
        val TicketDisplay: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
        val Date: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val Display: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, YYYY")
        val Day: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
        val Month: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
        val Year: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")
    }

    val readMore = "Read more"

    val eventDescriptionTitle = "What you need to know before you ask"

    val socialSubscriptionTitle = "Get notified about important conference updates"

    val organizersTitleSingle = "Organizer"
    val organizersTitleMultiple = "Organizers"

    val ticketsTitle = "Tickets"
    val ticketsNotification = "*Tickets grant access to all conference sections, coffee breaks, lunch and party. Accommodation is NOT included in the ticket price."
    val ticketsBuy = "Buy ticket"
    val ticketsGet = "Get ticket"
    val ticketsSold = "You missed it"
    val ticketsUpcoming = "Not available yet"

    val galleryFullCollection = "See all photos"

    val partnersDescriptionTitle = "Partners"
    val partnersJoin = "Become a partner"

    val venueTitle = "Venue"

    val copyright = "Inspired by Project Hoverboard"
}
