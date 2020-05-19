package ru.kugnn.microkonf.config.blocks

import kotlin.properties.Delegates

class PreviousStatistics {
    companion object {
        const val SOURCE = "/config/blocks/previous-statistics.yaml"
    }

    var enabled: Boolean = true
    lateinit var items: List<PreviousStatisticsItem>
}

class PreviousStatisticsItem {
    var number by Delegates.notNull<Int>()
    lateinit var label: String
}
