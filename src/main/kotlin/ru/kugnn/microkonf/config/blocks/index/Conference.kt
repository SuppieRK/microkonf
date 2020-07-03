package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.Utils.DayFormat
import ru.kugnn.microkonf.Utils.DisplayDateFormat
import ru.kugnn.microkonf.Utils.MonthFormat
import ru.kugnn.microkonf.Utils.YearFormat
import ru.kugnn.microkonf.Utils.getSortedDateBounds
import java.time.LocalDate
import java.time.temporal.ChronoField

@Introspected
data class Conference @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("city") var city: String,
        @JsonProperty("country") var country: String?,
        @JsonProperty("startDate") var start: String,
        @JsonProperty("endDate") var end: String?,
        @JsonProperty("description") var description: String,
        @JsonProperty("series") var series: Series?
) {
    @get:JsonIgnore
    @delegate:Transient
    val startDate: LocalDate by lazy {
        LocalDate.parse(start, Utils.ParseDateFormatter)
    }

    @get:JsonIgnore
    @delegate:Transient
    val endDate: LocalDate? by lazy {
        end?.run { LocalDate.parse(this, Utils.ParseDateFormatter) }
    }

    @get:JsonIgnore
    @delegate:Transient
    val conferenceWhere: String by lazy {
        if (country.isNullOrBlank()) {
            city
        } else {
            "$city, $country"
        }
    }

    @get:JsonIgnore
    @delegate:Transient
    val conferenceWhen: String by lazy {
        if (endDate == null || endDate == startDate) {
            DisplayDateFormat.format(startDate)
        } else {
            val (start, end) = getSortedDateBounds(startDate, endDate!!)

            when {
                start.get(ChronoField.YEAR) != end.get(ChronoField.YEAR) -> {
                    "${DisplayDateFormat.format(start)} - ${DisplayDateFormat.format(end)}"
                }
                start.get(ChronoField.MONTH_OF_YEAR) != end.get(ChronoField.MONTH_OF_YEAR) -> {
                    "${MonthFormat.format(start)} ${DayFormat.format(start)} - ${MonthFormat.format(end)} ${DayFormat.format(start)}, ${YearFormat.format(end)}"
                }
                else -> {
                    "${MonthFormat.format(start)} ${DayFormat.format(start)} - ${DayFormat.format(end)}, ${YearFormat.format(end)}"
                }
            }
        }
    }
}

@Introspected
data class Series @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("url") var url: String
)