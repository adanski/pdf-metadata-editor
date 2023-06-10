package app.pdfx

import app.pdfx.DateFormat.formatDateTime
import java.time.Instant

object ListFormat {
    fun humanReadable(list: List<Any>): String {
        val sb = StringBuilder()
        val it = list.iterator()
        while (it.hasNext()) {
            val v = it.next()
            if (Instant::class.java.isAssignableFrom(v.javaClass)) {
                sb.append(formatDateTime((v as Instant)))
            } else {
                sb.append(v.toString())
            }
            if (it.hasNext()) {
                sb.append(", ")
            }
        }
        return sb.toString()
    }
}
