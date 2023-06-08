package app.pdfx.annotations

@Retention(AnnotationRetention.RUNTIME)
annotation class FieldId(val value: String, val type: FieldType = FieldType.STRING) {
    enum class FieldType {
        STRING,
        TEXT,
        LONG,
        INT,
        DATE,
        BOOL
    }
}
