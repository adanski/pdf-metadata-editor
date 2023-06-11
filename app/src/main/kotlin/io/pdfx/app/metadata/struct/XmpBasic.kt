package io.pdfx.app.metadata.struct

import java.time.Instant

class XmpBasic {
    var creatorTool: String? = null
    var createDate: Instant? = null
    var modifyDate: Instant? = null
    var baseURL: String? = null
    var rating: Int? = null
    var label: String? = null
    var nickname: String? = null
    var identifiers: List<String>? = null
    var advisories: List<String>? = null
    var metadataDate: Instant? = null
}

class XmpBasicEnabled {
    var creatorTool = true
    var createDate = true
    var modifyDate = true
    var baseURL = true
    var rating = true
    var label = true
    var nickname = true
    var identifiers = true
    var advisories = true
    var metadataDate = true
    fun atLeastOne(): Boolean {
        return (creatorTool || createDate || modifyDate || baseURL || rating || label || nickname
                || identifiers || advisories || metadataDate)
    }

    fun setAll(value: Boolean) {
        creatorTool = value
        createDate = value
        modifyDate = value
        baseURL = value
        rating = value
        label = value
        nickname = value
        identifiers = value
        advisories = value
        metadataDate = value
    }
}
