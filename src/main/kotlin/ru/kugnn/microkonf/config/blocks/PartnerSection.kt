package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected

@Introspected
class PartnerSection {
    lateinit var name: String
    lateinit var items: List<PartnerInfo>

    @Introspected
    class PartnerInfo {
        lateinit var name: String
        lateinit var url: String
        lateinit var logo: String
    }
}