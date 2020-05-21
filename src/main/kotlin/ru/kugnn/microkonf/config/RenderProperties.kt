package ru.kugnn.microkonf.config

import io.micronaut.context.annotation.ConfigurationProperties

@ConfigurationProperties("render.blocks")
class RenderProperties {
    lateinit var index: List<String>
}