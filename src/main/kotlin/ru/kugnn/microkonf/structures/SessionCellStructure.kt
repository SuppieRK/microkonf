package ru.kugnn.microkonf.structures

import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.generateHash
import ru.kugnn.microkonf.render.CssGridAreaProperty

@Introspected
data class SessionCellStructure(
        val id: String,
        val title: String,
        val tracks: List<String>,
        val gridArea: CssGridAreaProperty
) {
    constructor(
            title: String,
            tracks: List<String>,
            gridArea: CssGridAreaProperty
    ) : this(
            generateHash(title),
            title,
            tracks,
            gridArea
    )
}