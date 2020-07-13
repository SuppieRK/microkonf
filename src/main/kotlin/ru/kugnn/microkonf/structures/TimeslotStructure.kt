package ru.kugnn.microkonf.structures

import io.micronaut.core.annotation.Introspected
import java.time.LocalTime

@Introspected
data class TimeslotStructure(
        val index: Int,
        val startsAt: LocalTime,
        val endsAt: LocalTime,
        val sessions: Map<String, SessionCellStructure>
)