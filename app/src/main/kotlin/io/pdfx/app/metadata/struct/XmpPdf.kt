package io.pdfx.app.metadata.struct

class XmpPdf {
    var pdfVersion: String? = null
    var keywords: String? = null
    var producer: String? = null
}

class XmpPdfEnabled {
    var pdfVersion = true
    var keywords = true
    var producer = true
    fun atLeastOne(): Boolean {
        return pdfVersion || keywords || producer
    }

    fun setAll(value: Boolean) {
        pdfVersion = value
        keywords = value
        producer = value
    }
}
