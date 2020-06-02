package ru.kugnn.microkonf.render

import io.micronaut.core.annotation.Introspected

/**
 * @see <a href="https://css-tricks.com/snippets/css/complete-guide-grid/">CSS Grid Area guide</a>
 */
@Introspected
data class GridArea(
        val rowStart: String = "auto",
        val columnStart: String = "auto",
        val rowEnd: String = "auto",
        val columnEnd: String = "auto"
) {
    constructor(
            rowStart: Int? = null,
            columnStart: Int? = null,
            rowEnd: Int? = null,
            columnEnd: Int? = null
    ) : this(
            rowStart = rowStart?.toString() ?: "auto",
            columnStart = columnStart?.toString() ?: "auto",
            rowEnd = rowEnd?.toString() ?: "auto",
            columnEnd = columnEnd?.toString() ?: "auto"
    )

    fun toStyleString(): String {
        return "grid-area: $rowStart / $columnStart / $rowEnd / $columnEnd"
    }
}