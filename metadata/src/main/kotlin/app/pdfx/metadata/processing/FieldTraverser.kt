package app.pdfx.metadata.processing

import app.pdfx.metadata.MetadataField
import app.pdfx.metadata.MetadataFieldName
import app.pdfx.metadata.MetadataFieldType
import app.pdfx.metadata.annotation.FieldId
import app.pdfx.metadata.annotation.MdStruct
import java.time.Instant
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeKind
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

internal class FieldTraverser(
    private val processingEnv: ProcessingEnvironment
) {

    fun traverseFields(
        ancestors: List<MetadataField>,
        all: Boolean,
        klass: TypeElement,
        mdType: MdStruct.Type,
        consumer: (List<MetadataField>) -> Unit
    ) {
        val fields: List<VariableElement> = klass.enclosedElements
            .filter {
                it is VariableElement
                        && it.kind == ElementKind.FIELD
                        && !it.modifiers.contains(Modifier.STATIC)
            }
            .map { it as VariableElement }

        processingEnv.messager.printNote("TRAVERSING ${klass.qualifiedName} BY ${fields.joinToString { it.simpleName }}")

        for (field in fields) {
            val mdStruct = field.getAnnotation(MdStruct::class.java)
            val fieldType: TypeMirror = field.asType()

            processingEnv.messager.printNote("FIELD ${field.simpleName} HAS ${mdStruct?.type}")
            if (mdStruct != null && mdStruct.type === mdType) {
                val prefix = if (ancestors.isNotEmpty()) "${ancestors[ancestors.size - 1].name.qualifiedName}." else ""
                val name = mdStruct.name.ifEmpty { field.simpleName }

                val t = createField(prefix + name, fieldType, null, mdStruct.access === MdStruct.Access.READ_WRITE)
                val fieldKlass: TypeElement = processingEnv.typeUtils.asElement(fieldType) as TypeElement
                traverseFields(ancestors + t, true, fieldKlass, mdType, consumer)
            } else {
                val fieldId = field.getAnnotation(FieldId::class.java)
                val isParentWritable = if (ancestors.isNotEmpty()) ancestors[ancestors.size - 1].writable else true
                if (fieldId != null) {
                    val prefix =
                        if (ancestors.isNotEmpty()) "${ancestors[ancestors.size - 1].name.qualifiedName}." else ""
                    val t = createField(prefix + fieldId.value, fieldType, fieldId.type, isParentWritable)
                    consumer(ancestors + t)
                } else if (all) {
                    val prefix =
                        if (ancestors.isNotEmpty()) "${ancestors[ancestors.size - 1].name.qualifiedName}." else ""
                    val t = createField(prefix + field.simpleName, fieldType, isParentWritable)
                    consumer(ancestors + t)
                }
            }
        }
    }

    private fun createField(
        qualifiedName: String,
        field: TypeMirror,
        type: MetadataFieldType?,
        writable: Boolean
    ): MetadataField {
        return MetadataField(
            name = MetadataFieldName(qualifiedName),
            type = type,
            writable = writable,
            list = isAssignable(field, List::class)
        )
    }

    private fun createField(qualifiedName: String, field: TypeMirror, writable: Boolean): MetadataField {
        val type = processingEnv.run {
            if (isAssignable(field, Boolean::class)) {
                MetadataFieldType.BOOL
            } else if (isAssignable(field, Instant::class)) {
                MetadataFieldType.DATE
            } else if (isAssignable(field, Int::class)) {
                MetadataFieldType.INT
            } else if (isAssignable(field, Long::class)) {
                MetadataFieldType.LONG
            } else {
                MetadataFieldType.STRING
            }
        }

        return MetadataField(
            name = MetadataFieldName(qualifiedName),
            type = type,
            writable = writable,
            list = processingEnv.run {
                isAssignable(field, List::class)
            }
        )
    }

    private fun isAssignable(first: TypeMirror, second: KClass<*>): Boolean {
        processingEnv.messager.printNote("isAssignable: first: $first, second: ${second.java.name}")

        return processingEnv.run {
            val second: TypeMirror = if (second.java.isPrimitive) {
                typeUtils.getPrimitiveType(TypeKind.valueOf(second.java.canonicalName.uppercase()))
            } else {
                elementUtils.getTypeElement(second.java.canonicalName).asType()
            }

            typeUtils.isAssignable(first, second)
        }
    }
}
