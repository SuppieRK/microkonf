package ru.kugnn.microkonf

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View
import ru.kugnn.microkonf.config.SiteProperties
import ru.kugnn.microkonf.config.pages.IndexProperties

@Controller
class SiteController(val siteProperties: SiteProperties) {
    companion object {
        const val IndexPageBlocksKey = "indexPageBlocks"
    }

    @Get
    @View("index")
    fun index(): Any {
        return siteProperties.asMap().plus(
                IndexPageBlocksKey to Utils.load(IndexProperties::class.java, IndexProperties.SOURCE)
        )
    }
}