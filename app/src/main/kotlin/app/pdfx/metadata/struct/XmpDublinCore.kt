package app.pdfx.metadata.struct

import app.pdfx.metadata.MetadataFieldType
import app.pdfx.metadata.annotation.FieldId
import java.time.Instant

class XmpDublinCore {
    var title: String? = null
    var description: String? = null
    var creators: List<String>? = null
    var contributors: List<String>? = null
    var coverage: String? = null

    @FieldId(value = "dates", type = MetadataFieldType.DATE)
    var dates: List<Instant>? = null
    var format: String? = null
    var identifier: String? = null
    var languages: List<String>? = null
    var publishers: List<String>? = null
    var relationships: List<String>? = null
    var rights: String? = null
    var source: String? = null
    var subjects: List<String>? = null
    var types: List<String>? = null
}

class XmpDublinCoreEnabled {
    var title = true
    var description = true
    var creators = true
    var contributors = true
    var coverage = true
    var dates = true
    var format = true
    var identifier = true
    var languages = true
    var publishers = true
    var relationships = true
    var rights = true
    var source = true
    var subjects = true
    var types = true
    fun atLeastOne(): Boolean {
        return (title || description || creators || contributors || coverage || dates || format || identifier
                || languages || publishers || relationships || rights || source || subjects || types)
    }

    fun setAll(value: Boolean) {
        title = value
        description = value
        creators = value
        contributors = value
        coverage = value
        dates = value
        format = value
        identifier = value
        languages = value
        publishers = value
        relationships = value
        rights = value
        source = value
        subjects = value
        types = value
    }
}
