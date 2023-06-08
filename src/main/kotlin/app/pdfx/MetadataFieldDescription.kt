package app.pdfx

import app.pdfx.annotations.FieldId
import java.lang.reflect.Field
import java.time.Instant
import java.util.*
import kotlin.reflect.full.isSuperclassOf

class MetadataFieldDescription {
    val name: String
    val field: Field
    val type: FieldId.FieldType?
    val isList: Boolean
    val isWritable: Boolean

    constructor(name: String, field: Field, type: FieldId.FieldType?, isWritable: Boolean) {
        this.name = name
        this.field = field
        this.type = type
        this.isWritable = isWritable
        isList = List::class.isSuperclassOf(field.type.kotlin)
    }

    constructor(name: String, field: Field, isWritable: Boolean) {
        val klass = field.type.kotlin
        type = if (Boolean::class.isSuperclassOf(klass)) {
            FieldId.FieldType.BOOL
        } else if (Instant::class.isSuperclassOf(klass)) {
            FieldId.FieldType.DATE
        } else if (Int::class.isSuperclassOf(klass)) {
            FieldId.FieldType.INT
        } else if (Long::class.isSuperclassOf(klass)) {
            FieldId.FieldType.LONG
        } else {
            FieldId.FieldType.STRING
        }
        this.name = name
        this.field = field
        this.isWritable = isWritable
        isList = List::class.isSuperclassOf(klass)
    }

    fun makeStringFromValue(value: Any?): String {
        if (value == null) {
            return ""
        }
        return if (isList) {
            ListFormat.humanReadable(value as List<Any>)
        } else if (type === FieldId.FieldType.DATE) {
            DateFormat.formatDateTime(value as Instant)
        } else if (type === FieldId.FieldType.BOOL) {
            if (value as Boolean) "true" else "false"
        } else {
            value.toString()
        }
    }

    fun makeValueFromString(value: String?): Any? {
        if (value == null) {
            return null
        }
        if (isList) {
            if (type === FieldId.FieldType.STRING) {
                return listOf(value)
            } else if (type === FieldId.FieldType.TEXT) {
                return listOf(*value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
            } else if (type === FieldId.FieldType.INT) {
                // TODO: possible allow comma separated interger list
                return listOf(value.toInt())
            } else if (type === FieldId.FieldType.BOOL) {
                // TODO: possible allow comma separated boolean list
                val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
                var b: Boolean? = null
                if (v == "true" || v == "yes") b = true
                if (v == "false" || v == "no") b = false
                return listOf(b)
            } else if (type === FieldId.FieldType.DATE) {
                val rval: MutableList<Instant> = mutableListOf()
                for (line in value.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
                    try {
                        rval.add(DateFormat.parseDate(line.trim { it <= ' ' }))
                    } catch (e: CommandLine.ParseError) {
                        throw RuntimeException("makeValueFromString() Invalid date format:$line")
                    }
                }
                return rval
            }
        } else {
            if (type === FieldId.FieldType.STRING) {
                return value
            } else if (type === FieldId.FieldType.TEXT) {
                return value
            } else if (type === FieldId.FieldType.INT) {
                return value.toInt()
            } else if (type === FieldId.FieldType.BOOL) {
                val v = value.lowercase(Locale.getDefault()).trim { it <= ' ' }
                if (v == "true" || v == "yes") return true
                return if (v == "false" || v == "no") false else null
            } else if (type === FieldId.FieldType.DATE) {
                return try {
                    DateFormat.parseDate(value)
                } catch (e: CommandLine.ParseError) {
                    throw RuntimeException("makeValueFromString() Invalid date format:$value")
                }
            }
        }
        throw RuntimeException("makeValueFromString() :Don't know how to convert to type:$type")
    }
}