package ru.kugnn.microkonf.config.blocks

import ru.kugnn.microkonf.Utils.getSortedDateBounds
import ru.kugnn.microkonf.config.SiteProperties.Companion.DateFormatter
import java.time.format.DateTimeFormatter

class ConferenceProperties {
    companion object {
        val Display: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM d, YYYY")
        val Day: DateTimeFormatter = DateTimeFormatter.ofPattern("d")
        val Month: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM")
        val Year: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY")

        const val SOURCE = "/config/blocks/conference.yaml"
    }

    lateinit var name: String
    lateinit var city: String
    var country: String? = null
    lateinit var startDate: String
    var endDate: String? = null
    lateinit var description: String
    var series: ConferenceSeries? = null

    // Complex calculable fields

    val conferenceWhere: String by lazy {
        if (country.isNullOrBlank()) {
            city
        } else {
            "$city, $country"
        }
    }

    val conferenceWhen: String by lazy {
        if (endDate.isNullOrBlank() || endDate == startDate) {
            Display.format(DateFormatter.parse(startDate))
        } else {
            val (start, end) = getSortedDateBounds(startDate, endDate!!, DateFormatter)

            when {
                start.year != end.year -> "${Display.format(start)} - ${Display.format(end)}"
                start.month != end.month -> "${Month.format(start)} ${Day.format(start)} - ${Month.format(end)} ${Day.format(start)}, ${Year.format(end)}"
                else -> "${Month.format(start)} ${Day.format(start)} - ${Day.format(end)}, ${Year.format(end)}"
            }
        }
    }
}

class ConferenceSeries {
    lateinit var name: String
    lateinit var url: String
}
