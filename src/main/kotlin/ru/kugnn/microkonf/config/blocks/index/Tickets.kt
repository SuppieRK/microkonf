package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.Utils.ParseDateFormatter
import ru.kugnn.microkonf.Utils.TicketDateFormat
import java.time.LocalDate

@Introspected
data class Tickets @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean = false,
        @JsonProperty("free") var free: Boolean = false,
        @JsonProperty("orderUrl") var orderUrl: String,
        @JsonProperty("items") var items: List<Item>
)

@Introspected
data class Item @JsonCreator constructor(
        @JsonProperty("title") var title: String,
        @JsonProperty("price") var price: String,
        @JsonProperty("featureText") var featureText: String,
        @JsonProperty("startDate") var start: String,
        @JsonProperty("endDate") var end: String,
        @JsonProperty("note") var note: String
) {
    @get:JsonIgnore
    @delegate:Transient
    val startDate: LocalDate by lazy {
        LocalDate.parse(start, ParseDateFormatter)
    }

    @get:JsonIgnore
    @delegate:Transient
    val endDate: LocalDate by lazy {
        LocalDate.parse(end, ParseDateFormatter)
    }

    // Complex calculable fields
    @get:JsonIgnore
    @delegate:Transient
    val upcoming: Boolean by lazy {
        LocalDate.now().run {
            startDate.isBefore(this) && endDate.isBefore(this)
        }
    }

    @get:JsonIgnore
    @delegate:Transient
    val missed: Boolean by lazy {
        LocalDate.now().run {
            startDate.isAfter(this) && endDate.isAfter(this)
        }
    }

    @get:JsonIgnore
    @delegate:Transient
    val active: Boolean by lazy {
        !missed && !upcoming
    }

    @get:JsonIgnore
    @delegate:Transient
    val saleFor: String by lazy {
        val (start, end) = Utils.getSortedDateBounds(startDate, endDate)
        "${TicketDateFormat.format(start)} - ${TicketDateFormat.format(end)}"
    }
}