package ru.kugnn.microkonf.config.blocks.index

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.DayFormat
import ru.kugnn.microkonf.Utils.DisplayDateFormat
import ru.kugnn.microkonf.Utils.MonthFormat
import ru.kugnn.microkonf.Utils.ParseDateFormat
import ru.kugnn.microkonf.Utils.YearFormat
import ru.kugnn.microkonf.Utils.getSortedDateBounds
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.temporal.ChronoField

@Introspected
data class Conference @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("city") var city: String,
        @JsonProperty("country") var country: String?,
        @JsonProperty("startDate") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ParseDateFormat) var startDate: LocalDate,
        @JsonProperty("endDate") @JsonDeserialize(using = LocalDateDeserializer::class) @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ParseDateFormat) var endDate: LocalDate?,
        @JsonProperty("description") var description: String,
        @JsonProperty("series") var series: Series?
) {
    @get:JsonIgnore
    val conferenceWhere: String by lazy {
        if (country.isNullOrBlank()) {
            city
        } else {
            "$city, $country"
        }
    }

    @get:JsonIgnore
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

    fun toDto(): ConferenceDto {
        return ConferenceDto(
                name,
                city,
                country,
                startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
                endDate?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli(),
                description,
                series?.toDto()
        )
    }

    companion object {
        fun fromDto(dto: ConferenceDto): Conference {
            return Conference(
                    dto.name,
                    dto.city,
                    dto.country,
                    LocalDate.ofInstant(Instant.ofEpochMilli(dto.startDate), ZoneOffset.UTC),
                    dto.endDate?.run { LocalDate.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC) },
                    dto.description,
                    dto.series?.run { Series.fromDto(this) }
            )
        }
    }
}

@Introspected
data class Series @JsonCreator constructor(
        @JsonProperty("name") var name: String,
        @JsonProperty("url") var url: String
) {
    fun toDto(): ConferenceDto.SeriesDto {
        return ConferenceDto.SeriesDto(name, url)
    }

    companion object {
        fun fromDto(dto: ConferenceDto.SeriesDto): Series {
            return Series(dto.name, dto.url)
        }
    }
}

@Introspected
data class ConferenceDto(
        var name: String,
        var city: String,
        var country: String?,
        var startDate: Long,
        var endDate: Long?,
        var description: String,
        var series: SeriesDto?
) {
    @Introspected
    data class SeriesDto(
            var name: String,
            var url: String
    )
}