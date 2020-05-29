package ru.kugnn.microkonf.config

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.Duration
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

class SimpleDurationDeserializer : JsonDeserializer<Duration>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Duration? {
        return p?.run { parseDuration(p.text) }
    }

    private fun parseDuration(input: String): Duration {
        val originalUnitString: String = getUnits(input)!!
        var unitString: String = originalUnitString
        val numberString: String = input.substring(0, input.length - unitString.length)

        // this would be caught later anyway, but the error message
        // is more helpful if we check it here.
        require(numberString.isNotBlank()) {
            "No number in duration value '$input'"
        }

        if (unitString.length > 2 && !unitString.endsWith("s")) {
            unitString += "s"
        }

        // note that this is deliberately case-sensitive
        val units = when (unitString) {
            "ms", "millis", "milliseconds" -> TimeUnit.MILLISECONDS
            "us", "micros", "microseconds" -> TimeUnit.MICROSECONDS
            "ns", "nanos", "nanoseconds" -> TimeUnit.NANOSECONDS
            "d", "days" -> TimeUnit.DAYS
            "h", "hours" -> TimeUnit.HOURS
            "s", "seconds" -> TimeUnit.SECONDS
            "", "m", "minutes" -> TimeUnit.MINUTES
            else -> throw IllegalArgumentException("Could not parse time unit '$originalUnitString' (try ns, us, ms, s, m, h, d)")
        }

        return Duration.ofNanos(
                try {
                    if (pattern.matcher(numberString).matches()) {
                        units.toNanos(numberString.toLong())
                    } else {
                        (numberString.toDouble() * units.toNanos(1)).toLong()
                    }
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Could not parse duration number '$numberString'")
                }
        )
    }

    private fun getUnits(s: String): String? {
        var i = s.length - 1
        while (i >= 0) {
            val c = s[i]
            if (!Character.isLetter(c)) break
            i -= 1
        }
        return s.substring(i + 1)
    }

    companion object {
        val pattern: Pattern = Pattern.compile("[+-]?[0-9]+")
    }
}