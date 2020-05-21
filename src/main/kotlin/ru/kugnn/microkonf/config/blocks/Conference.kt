package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.getSortedDateBounds
import ru.kugnn.microkonf.config.SiteConstants.Companion.Date
import ru.kugnn.microkonf.config.SiteConstants.Companion.Day
import ru.kugnn.microkonf.config.SiteConstants.Companion.Display
import ru.kugnn.microkonf.config.SiteConstants.Companion.Month
import ru.kugnn.microkonf.config.SiteConstants.Companion.Year

@Introspected
class Conference {
    lateinit var name: String
    lateinit var city: String
    var country: String? = null
    lateinit var startDate: String
    var endDate: String? = null
    lateinit var description: String
    var series: ConferenceSeries? = null

    val conferenceWhere: String by lazy {
        if (country.isNullOrBlank()) {
            city
        } else {
            "$city, $country"
        }
    }

    val conferenceWhen: String by lazy {
        if (endDate.isNullOrBlank() || endDate == startDate) {
            Display.format(Date.parse(startDate))
        } else {
            val (start, end) = getSortedDateBounds(startDate, endDate!!, Date)

            when {
                start.year != end.year -> "${Display.format(start)} - ${Display.format(end)}"
                start.month != end.month -> "${Month.format(start)} ${Day.format(start)} - ${Month.format(end)} ${Day.format(start)}, ${Year.format(end)}"
                else -> "${Month.format(start)} ${Day.format(start)} - ${Day.format(end)}, ${Year.format(end)}"
            }
        }
    }

    //    @Introspected
    class ConferenceSeries {
        lateinit var name: String
        lateinit var url: String
    }
}