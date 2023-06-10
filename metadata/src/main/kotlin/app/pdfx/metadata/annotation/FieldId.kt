package app.pdfx.metadata.annotation

import app.pdfx.metadata.MetadataFieldType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY)
annotation class FieldId(val value: String, val type: MetadataFieldType = MetadataFieldType.STRING)
