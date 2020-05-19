package ru.kugnn.microkonf.config.blocks

class PartnerProperties {
    companion object {
        const val SOURCE = "/config/blocks/partners.yaml"
    }

    var partners: List<PartnerSection> = ArrayList()
}

class PartnerSection {
    lateinit var name: String
    var items: List<PartnerInfo> = ArrayList()
}

class PartnerInfo {
    lateinit var name: String
    lateinit var url: String
    lateinit var logo: String
}
