package ru.kugnn.microkonf.config.blocks.sessions

import ru.kugnn.microkonf.config.Identifiable

abstract class GenericSession : Identifiable() {
    abstract val title: String

    override fun getIdentifier(): String = title
}