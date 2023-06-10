package app.pdfx.metadata

data class MetadataField(
    val name: MetadataFieldName,
    val type: MetadataFieldType?,
    val list: Boolean,
    val writable: Boolean
) {

    fun set(obj: Any, value: Any?) {
        obj::class.java.getDeclaredField(name.name).set(obj, value)
    }

    fun <T> get(obj: Any): T? {
        return obj::class.java.getDeclaredField(name.name).get(obj) as T?
    }
}

data class MetadataFieldName(
    /**
     * Field name
     */
    val name: String,
    /**
     * Qualified name or a 'path' to the field in the class hierarchy.
     * Individual parts are separated with dots.
     */
    val qualifiedName: String
) {
    constructor(qualifiedName: String) : this(qualifiedName.substringAfterLast('.'), qualifiedName)
}

enum class MetadataFieldType {
    STRING,
    TEXT,
    LONG,
    INT,
    DATE,
    BOOL
}
