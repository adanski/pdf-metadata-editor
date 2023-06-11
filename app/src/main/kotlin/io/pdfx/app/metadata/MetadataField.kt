package io.pdfx.app.metadata

import io.pdfx.app.CommandLine
import io.pdfx.app.DateFormat
import io.pdfx.app.ListFormat
import io.pdfx.metadata.MetadataField
import io.pdfx.metadata.MetadataFieldType
import java.time.Instant
import java.util.*

fun MetadataField.makeStringFromValue(value: Any?): String {
    if (value == null) {
        return ""
    }
    return if (list) {
        ListFormat.humanReadable(value as List<Any>)
    } else if (type === MetadataFieldType.DATE) {
        DateFormat.formatDateTime(value as Instant)
    } else if (type === MetadataFieldType.BOOL) {
        if (value as Boolean) "true" else "false"
    } else {
        value.toString()
    }
}

fun MetadataField.makeValueFromString(value: String?): Any? {
    if (value == null) {
        return null
    }
    if (list) {
        if (type === MetadataFieldType.STRING) {
            return listOf(value)
        } else if (type === MetadataFieldType.TEXT) {
            return listOf(*value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
        } else if (type === MetadataFieldType.INT) {
            // TODO: possible allow comma separated interger list
            return listOf(value.toInt())
        } else if (type === MetadataFieldType.BOOL) {
            // TODO: possible allow comma separated boolean list
            val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
            var b: Boolean? = null
            if (v == "true" || v == "yes") b = true
            if (v == "false" || v == "no") b = false
            return listOf(b)
        } else if (type === MetadataFieldType.DATE) {
            val rval: MutableList<Instant> = mutableListOf()
            for (line in value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                try {
                    rval.add(DateFormat.parseDate(line.trim { it <= ' ' }))
                } catch (e: CommandLine.ParseError) {
                    throw IllegalArgumentException("Invalid date format: $line")
                }
            }
            return rval
        }
    } else {
        if (type === MetadataFieldType.STRING) {
            return value
        } else if (type === MetadataFieldType.TEXT) {
            return value
        } else if (type === MetadataFieldType.INT) {
            return value.toInt()
        } else if (type === MetadataFieldType.BOOL) {
            val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
            if (v == "true" || v == "yes") return true
            return if (v == "false" || v == "no") false else null
        } else if (type === MetadataFieldType.DATE) {
            return try {
                DateFormat.parseDate(value)
            } catch (e: CommandLine.ParseError) {
                throw IllegalArgumentException("Invalid date format: $value")
            }
        }
    }
    throw IllegalStateException("Unable to convert to type: $type")
}
