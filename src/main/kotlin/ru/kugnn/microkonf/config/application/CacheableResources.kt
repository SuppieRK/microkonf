package ru.kugnn.microkonf.config.application

import io.micronaut.context.annotation.ConfigurationProperties
import java.io.File

@ConfigurationProperties("render.cache")
class CacheableResources {
    lateinit var folder: String
    lateinit var excludeHaving: List<String>

    val list by lazy {
        getAllResourcesFrom(folder, excludeHaving).toSet() + "/"
    }

    private fun getAllResourcesFrom(folderName: String, excludeHaving: List<String>): List<String> {
        return this.javaClass.classLoader.getResource(folderName)?.path?.run {
            recursiveFolderScan(folderName, File(this), excludeHaving)
        } ?: emptyList()
    }

    private fun recursiveFolderScan(folderName: String, folder: File, excludeHaving: List<String>): List<String> {
        return folder.listFiles()?.flatMap {
            if (it.isDirectory) {
                if (excludeHaving.any { toExclude -> it.path.contains(toExclude) }) {
                    emptyList()
                } else {
                    recursiveFolderScan(folderName, it, excludeHaving)
                }
            } else {
                listOf(("/" + folderName + it.path.substringAfter(folderName)).replace('\\', '/'))
            }
        } ?: emptyList()
    }
}