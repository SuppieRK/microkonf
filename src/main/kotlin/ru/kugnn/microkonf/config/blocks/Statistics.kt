package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected
import kotlin.properties.Delegates

@Introspected
class Statistics {
    var enabled: Boolean = true
    lateinit var items: List<PreviousStatisticsItem>

    @Introspected
    class PreviousStatisticsItem {
        var number by Delegates.notNull<Int>()
        lateinit var label: String
    }
}