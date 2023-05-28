package app.pdfx

import app.pdfx.CommandLine.ParseError
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateFormat {
    private val isoDateFormat = arrayOf(
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
        SimpleDateFormat("yyyy-MM-dd")
    )

    @JvmStatic
    fun parseDateOrNull(value: String): Calendar? {
        return try {
            parseDate(value)
        } catch (e: ParseError) {
            null
        }
    }

    @JvmStatic
    @Throws(ParseError::class)
    fun parseDate(value: String): Calendar {
        var d: Date? = null
        for (df in isoDateFormat) {
            try {
                d = df.parse(value)
            } catch (e: ParseException) {
            }
        }
        if (d != null) {
            val cal = Calendar.getInstance()
            cal.time = d
            return cal
        }
        throw ParseError("Invalid date format: $value")
    }

    fun formatDate(cal: Calendar): String {
        return isoDateFormat[3].format(cal.time)
    }

    @JvmStatic
    fun formatDateTime(cal: Calendar): String {
        return isoDateFormat[1].format(cal.time)
    }

    @JvmStatic
    fun formatDateTimeFull(cal: Calendar): String {
        return isoDateFormat[0].format(cal.time)
    }
}
