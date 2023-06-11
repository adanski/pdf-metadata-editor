package app.pdfx.metadata

import java.lang.reflect.Field

data class MetadataField(
    val reference: MetadataFieldReference,
    val type: MetadataFieldType?,
    val list: Boolean,
    val writable: Boolean
) {

    private val field: Field by lazy(LazyThreadSafetyMode.PUBLICATION) {
        val field = Class.forName(reference.ownerQualifiedName).getDeclaredField(reference.name)
        field.isAccessible = true
        field
    }

    fun set(obj: Any, value: Any?) {
        field.set(obj, value)
    }

    fun <T> get(obj: Any): T? {
        return field.get(obj) as T?
    }
}

data class MetadataFieldReference(
    /**
     * Field name
     */
    val name: String,
    /**
     * Qualified name or a 'path' this field refers to in the class hierarchy.
     * Individual parts are separated with dots.
     */
    val referencePath: String,
    /**
     * Qualified name of the owning class.
     */
    val ownerQualifiedName: String,
)

enum class MetadataFieldType {
    STRING,
    TEXT,
    LONG,
    INT,
    DATE,
    BOOL
}
