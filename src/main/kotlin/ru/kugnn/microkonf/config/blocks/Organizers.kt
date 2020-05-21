package ru.kugnn.microkonf.config.blocks

import io.micronaut.core.annotation.Introspected

@Introspected
class Organizers {
    lateinit var description: String
    lateinit var userGroups: List<Organizer>

    @Introspected
    class Organizer {
        lateinit var shortName: String
        lateinit var fullName: String
        lateinit var logo: String
        lateinit var photo: String
        var email: String? = null
        lateinit var description: String
        lateinit var organizationUrl: String
        lateinit var links: List<OrganizerSocials>
    }

    @Introspected
    class OrganizerSocials {
        lateinit var type: String
        lateinit var url: String
        var featured: Boolean = false
    }
}