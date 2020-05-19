package ru.kugnn.microkonf

import org.slf4j.LoggerFactory
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Utils {
    val log = LoggerFactory.getLogger(Utils::class.java)

    fun <T> lazyLoader(configurationClass: Class<T>, configurationPath: String): Lazy<T> = lazy {
        load(configurationClass, configurationPath)
    }

    fun <T> load(configurationClass: Class<T>, configurationPath: String): T {
        val text = Utils::class.java.classLoader.getResource(configurationPath)?.readText()

        if (text.isNullOrBlank()) {
            log.error("Unable to read $configurationPath")
            throw Exception()
        } else {
            return Yaml(Constructor(configurationClass)).loadAs(text, configurationClass)
        }
    }

    fun getSortedDateBounds(startDate: String, endDate: String, formatter: DateTimeFormatter): Pair<LocalDateTime, LocalDateTime> {
        var start = LocalDate.parse(startDate, formatter).atStartOfDay()
        var end = LocalDate.parse(endDate, formatter).atStartOfDay()

        if (start.isAfter(end)) {
            start = end.also { end = start }
        }

        return Pair(start, end)
    }
}
