package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.Utils.ParseDateFormat
import ru.kugnn.microkonf.Utils.TicketDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Introspected
data class Tickets @JsonCreator constructor(
        @JsonProperty("enabled") var enabled: Boolean = false,
        @JsonProperty("free") var free: Boolean = false,
        @JsonProperty("orderUrl") var orderUrl: String,
        @JsonProperty("items") var items: List<Item>
) {
    fun toDto(): TicketsDto {
        return TicketsDto(
                enabled,
                free,
                orderUrl,
                items.map { it.toDto() }
        )
    }

    companion object {
        fun fromDto(dto: TicketsDto): Tickets {
            return Tickets(
                    dto.enabled,
                    dto.free,
                    dto.orderUrl,
                    dto.items.map { Item.fromDto(it) }
            )
        }
    }

    @Introspected
    data class Item @JsonCreator constructor(
            @JsonProperty("title") var title: String,
            @JsonProperty("price") var price: String,
            @JsonProperty("featureText") var featureText: String,
            @JsonProperty("startDate") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ParseDateFormat) var startDate: LocalDate,
            @JsonProperty("endDate") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ParseDateFormat) var endDate: LocalDate,
            @JsonProperty("note") var note: String
    ) {
        // Complex calculable fields
        @get:JsonIgnore
        val upcoming: Boolean by lazy {
            LocalDate.now().run {
                startDate.isBefore(this) && endDate.isBefore(this)
            }
        }

        @get:JsonIgnore
        val missed: Boolean by lazy {
            LocalDate.now().run {
                startDate.isAfter(this) && endDate.isAfter(this)
            }
        }

        @get:JsonIgnore
        val active: Boolean by lazy {
            !missed && !upcoming
        }

        @get:JsonIgnore
        val saleFor: String by lazy {
            val (start, end) = Utils.getSortedDateBounds(startDate, endDate)
            "${TicketDateFormat.format(start)} - ${TicketDateFormat.format(end)}"
        }

        fun toDto(): TicketsDto.ItemDto {
            return TicketsDto.ItemDto(
                    title,
                    price,
                    featureText,
                    startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    endDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                    note
            )
        }

        companion object {
            fun fromDto(dto: TicketsDto.ItemDto): Item {
                return Item(
                        dto.title,
                        dto.price,
                        dto.featureText,
                        LocalDate.ofInstant(Instant.ofEpochMilli(dto.startDate), ZoneOffset.UTC),
                        LocalDate.ofInstant(Instant.ofEpochMilli(dto.endDate), ZoneOffset.UTC),
                        dto.note
                )
            }
        }
    }
}

@Introspected
data class TicketsDto(
        var enabled: Boolean,
        var free: Boolean,
        var orderUrl: String,
        var items: List<ItemDto>
) {
    @Introspected
    data class ItemDto(
            var title: String,
            var price: String,
            var featureText: String,
            var startDate: Long,
            var endDate: Long,
            var note: String
    )
}