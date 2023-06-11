package io.pdfx.metadata.annotation

import io.pdfx.metadata.MetadataFieldType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class FieldId(val value: String, val type: MetadataFieldType = MetadataFieldType.STRING)
