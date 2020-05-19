package ru.kugnn.microkonf.config.blocks

import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.config.SiteProperties.Companion.DateFormatter
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.max
import kotlin.math.min

class TicketProperties {
    companion object {
        const val SOURCE = "/config/blocks/tickets.yaml"
    }

    lateinit var orderUrl: String

    var enabled: Boolean = false
    var free: Boolean = false

    var items: List<TicketItem> = ArrayList()
        get() = field.sortedBy { LocalDate.parse(it.startDate, DateFormatter) }
}

class TicketItem {
    companion object {
        val Display: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d")
    }

    lateinit var title: String
    lateinit var price: String
    lateinit var featureText: String
    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var note: String

    // Complex calculable fields

    val upcoming: Boolean by lazy {
        System.currentTimeMillis() < min(
                LocalDate.parse(startDate, DateFormatter).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                LocalDate.parse(endDate, DateFormatter).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        )
    }

    val missed: Boolean by lazy {
        System.currentTimeMillis() > max(
                LocalDate.parse(startDate, DateFormatter).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                LocalDate.parse(endDate, DateFormatter).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        )
    }

    val active: Boolean by lazy {
        !missed && !upcoming
    }

    val saleFor: String by lazy {
        val (start, end) = Utils.getSortedDateBounds(startDate, endDate, DateFormatter)
        "${Display.format(start)} - ${Display.format(end)}"
    }
}
