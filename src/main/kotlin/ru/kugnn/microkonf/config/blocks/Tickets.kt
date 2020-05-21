package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils
import ru.kugnn.microkonf.config.SiteConstants.Companion.Date
import ru.kugnn.microkonf.config.SiteConstants.Companion.TicketDisplay
import java.time.LocalDate
import java.time.ZoneOffset
import kotlin.math.max
import kotlin.math.min

@Introspected
class Tickets {
    var enabled: Boolean = false
    var free: Boolean = false
    lateinit var orderUrl: String
    lateinit var items: List<TicketItem>

    @Introspected
    class TicketItem {
        lateinit var title: String
        lateinit var price: String
        lateinit var featureText: String
        lateinit var startDate: String
        lateinit var endDate: String
        lateinit var note: String

        // Complex calculable fields

        val upcoming: Boolean by lazy {
            System.currentTimeMillis() < min(
                    LocalDate.parse(startDate, Date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDate.parse(endDate, Date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        val missed: Boolean by lazy {
            System.currentTimeMillis() > max(
                    LocalDate.parse(startDate, Date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli(),
                    LocalDate.parse(endDate, Date).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
            )
        }

        val active: Boolean by lazy {
            !missed && !upcoming
        }

        val saleFor: String by lazy {
            val (start, end) = Utils.getSortedDateBounds(startDate, endDate, Date)
            "${TicketDisplay.format(start)} - ${TicketDisplay.format(end)}"
        }
    }
}