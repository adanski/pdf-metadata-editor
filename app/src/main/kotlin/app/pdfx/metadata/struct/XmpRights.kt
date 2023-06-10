package app.pdfx.metadata.struct

class XmpRights {
    var certificate: String? = null
    var marked: Boolean? = null
    var owner: List<String>? = null
    var usageTerms: String? = null
    var webStatement: String? = null
}

class XmpRightsEnabled {
    var certificate = true
    var marked = true
    var owner = true
    var usageTerms = true
    var webStatement = true
    fun atLeastOne(): Boolean {
        return certificate || marked || owner || usageTerms || webStatement
    }

    fun setAll(value: Boolean) {
        certificate = value
        marked = value
        owner = value
        usageTerms = value
        webStatement = value
    }
}
