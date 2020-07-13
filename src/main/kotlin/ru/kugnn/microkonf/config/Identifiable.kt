package ru.kugnn.microkonf.config

import com.fasterxml.jackson.annotation.JsonIgnore
import io.micronaut.core.annotation.Introspected
import ru.kugnn.microkonf.Utils.generateHash

@Introspected
abstract class Identifiable {
    @JsonIgnore
    abstract fun getIdentifier(): String

    @get:JsonIgnore
    val id: String by lazy {
        generateHash(getIdentifier() + hashCode())
    }
}