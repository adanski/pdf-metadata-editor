package app.pdfx.metadata.struct

import java.time.Instant

class Basic {
    var title: String? = null
    var author: String? = null
    var subject: String? = null
    var keywords: String? = null
    var creator: String? = null
    var producer: String? = null
    var creationDate: Instant? = null
    var modificationDate: Instant? = null
    var trapped: String? = null
}

class BasicEnabled {
    var title = true
    var author = true
    var subject = true
    var keywords = true
    var creator = true
    var producer = true
    var creationDate = true
    var modificationDate = true
    var trapped = true
    fun atLeastOne(): Boolean {
        return (title || author || subject || keywords || creator || producer || creationDate || modificationDate
                || trapped)
    }

    fun setAll(value: Boolean) {
        title = value
        author = value
        subject = value
        keywords = value
        creator = value
        producer = value
        creationDate = value
        modificationDate = value
        trapped = value
    }
}
