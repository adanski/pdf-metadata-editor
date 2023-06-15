package io.pdfx.common.metadata.annotation

import io.pdfx.common.metadata.MetadataFieldType

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class FieldId(val value: String, val type: MetadataFieldType = MetadataFieldType.STRING)
