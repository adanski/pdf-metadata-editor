package app.pdfx.metadata.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class FieldEnabled(val value: String)
