package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.ParseDateFormat
import ru.kugnn.microkonf.Utils.DayFormat
import ru.kugnn.microkonf.Utils.DisplayDateFormat
import ru.kugnn.microkonf.Utils.MonthDormat
import ru.kugnn.microkonf.Utils.YearFormat
import ru.kugnn.microkonf.Utils.getSortedDateBounds

@Introspected
data class Conference @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("city") var city: String,
        @JsonProperty("country") var country: String?,
        @JsonProperty("startDate") var startDate: String,
        @JsonProperty("endDate") var endDate: String?,
        @JsonProperty("description") var description: String,
        @JsonProperty("series") var series: ConferenceSeries?
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
            DisplayDateFormat.format(ParseDateFormat.parse(startDate))
        } else {
            val (start, end) = getSortedDateBounds(startDate, endDate!!, ParseDateFormat)

            when {
                start.year != end.year -> "${DisplayDateFormat.format(start)} - ${DisplayDateFormat.format(end)}"
                start.month != end.month -> "${MonthDormat.format(start)} ${DayFormat.format(start)} - ${MonthDormat.format(end)} ${DayFormat.format(start)}, ${YearFormat.format(end)}"
                else -> "${MonthDormat.format(start)} ${DayFormat.format(start)} - ${DayFormat.format(end)}, ${YearFormat.format(end)}"
            }
        }
    }

    @Introspected
    data class ConferenceSeries @JsonCreator constructor(
            @JsonProperty("name") var name: String,
            @JsonProperty("url") var url: String
    )
}