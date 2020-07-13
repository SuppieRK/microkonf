package ru.kugnn.microkonf.render

import io.micronaut.core.annotation.Introspected

/**
 * @see <a href="https://css-tricks.com/snippets/css/complete-guide-grid/">CSS Grid Area guide</a>
 */
@Introspected
data class CssGridAreaProperty(
        val rowStart: Int,
        val columnStart: Int,
        val rowEnd: Int? = null,
        val columnEnd: Int? = null
) {
    fun toStyleString(): String {
        return "grid-area: $rowStart / $columnStart / ${rowEnd?.toString() ?: "auto"} / ${columnEnd?.toString() ?: "auto"}"
    }
}