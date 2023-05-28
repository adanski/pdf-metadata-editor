package app.pdfx

@Retention(AnnotationRetention.RUNTIME)
annotation class FieldID(val value: String, val type: FieldType = FieldType.STRING) {
    enum class FieldType {
        STRING,
        TEXT,
        LONG,
        INT,
        DATE,
        BOOL
    }
}
