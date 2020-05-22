package ru.kugnn.microkonf.config.blocks

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.Utils.ParseDateFormat
import ru.kugnn.microkonf.Utils.TicketDateFormat
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.max
import kotlin.math.min

@Introspected
data class Tickets @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean = false,
        @JsonProperty("free") var free: Boolean = false,
        @JsonProperty("orderUrl") var orderUrl: String,
        @JsonProperty("items") var items: List<TicketItem>
) {
    @Introspected
    data class TicketItem @JsonCreator constructor(
            @JsonProperty("title") var title: String,
            @JsonProperty("price") var price: String,
            @JsonProperty("featureText") var featureText: String,
            @JsonProperty("startDate") var startDate: String,
            @JsonProperty("endDate") var endDate: String,
            @JsonProperty("note") var note: String
    ) {
        // Complex calculable fields
        val upcoming: Boolean by lazy {
            System.currentTimeMillis() < min(
                    LocalDate.parse(startDate, ParseDateFormat).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDate.parse(endDate, ParseDateFormat).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        val missed: Boolean by lazy {
            System.currentTimeMillis() > max(
                    LocalDate.parse(startDate, ParseDateFormat).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDate.parse(endDate, ParseDateFormat).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        val active: Boolean by lazy {
            !missed && !upcoming
        }

        val saleFor: String by lazy {
            val (start, end) = Utils.getSortedDateBounds(startDate, endDate, ParseDateFormat)
            "${TicketDateFormat.format(start)} - ${TicketDateFormat.format(end)}"
        }
    }
}