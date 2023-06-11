package io.pdfx.app

import io.pdfx.app.CommandLine.ParseError
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAccessor
import java.util.*

object DateFormat {
    private val DATE_FORMATS = arrayOf(
        DateTimeFormatter.ISO_INSTANT,
        DateTimeFormatter.ISO_LOCAL_DATE_TIME,
        formatterBuilderForDate()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendOptional(DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendLiteral(' ')
                .append(DateTimeFormatter.ISO_LOCAL_TIME)
                .toFormatter())
            .toFormatter(),
        formatterBuilderForDate()
            .append(DateTimeFormatter.ISO_LOCAL_DATE)
            .appendLiteral('T')
            .toFormatter()
    )

    private fun formatterBuilderForDate(): DateTimeFormatterBuilder {
        return DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.MILLI_OF_SECOND, 0)
    }

    fun parseDateOrNull(value: String): Instant? {
        return try {
            parseDate(value)
        } catch (e: ParseError) {
            null
        }
    }

    fun parseDate(value: String): Instant {
        var d: TemporalAccessor? = null
        for (df in DATE_FORMATS) {
            try {
                d = ZonedDateTime.of(LocalDateTime.parse(value, df), ZoneOffset.UTC)
                break
            } catch (_: DateTimeParseException) {
            }
        }
        if (d != null) {
            return Instant.from(d)
        }
        throw ParseError("Invalid date format: ${value}")
    }

    fun formatDate(instant: Instant): String {
        return DATE_FORMATS[2].format(instant)
    }

    fun formatDateTime(instant: Instant): String {
        return DATE_FORMATS[1].format(instant)
    }

    fun formatDateTimeFull(instant: Instant): String {
        return DATE_FORMATS[0].format(instant)
    }
}

fun Instant?.toCalendar(): Calendar? {
    return if (this == null)
        null
    else
        Calendar.getInstance().also {
            it.time = Date.from(this)
        }
}

fun Collection<Calendar>?.toInstants(): List<Instant>? {
    return this?.map { it.toInstant() }
}
