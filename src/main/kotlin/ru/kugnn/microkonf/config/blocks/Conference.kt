package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.getSortedDateBounds
import ru.kugnn.microkonf.config.SiteConstants.Companion.Date
import ru.kugnn.microkonf.config.SiteConstants.Companion.Day
import ru.kugnn.microkonf.config.SiteConstants.Companion.Display
import ru.kugnn.microkonf.config.SiteConstants.Companion.Month
import ru.kugnn.microkonf.config.SiteConstants.Companion.Year

@Introspected
data class Conference @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("city") var city: String,
        @JsonProperty("country") var country: String? = null,
        @JsonProperty("startDate") var startDate: String,
        @JsonProperty("endDate") var endDate: String? = null,
        @JsonProperty("description") var description: String,
        @JsonProperty("series") var series: ConferenceSeries? = null
) {
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

    @Introspected
    data class ConferenceSeries @JsonCreator constructor(
            @JsonProperty("name") var name: String,
            @JsonProperty("url") var url: String
    )
}