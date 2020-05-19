package ru.kugnn.microkonf.config.blocks

class OrganizerProperties {
    companion object {
        const val SOURCE = "/config/blocks/organizers.yaml"
    }

    lateinit var description: String
    var userGroups: List<Organizer> = ArrayList()
}

class Organizer {
    lateinit var shortName: String
    lateinit var fullName: String
    lateinit var logo: String
    lateinit var photo: String
    lateinit var eMail: String
    lateinit var links: List<OrganizerSocials>
    lateinit var description: String
    lateinit var organizationUrl: String
}

class OrganizerSocials {
    lateinit var type: String
    lateinit var url: String
    var featured: Boolean = false
}
