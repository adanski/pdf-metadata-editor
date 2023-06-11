package app.pdfx.metadata.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class FieldEnabled(val value: String)
