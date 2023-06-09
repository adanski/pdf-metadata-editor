package app.pdfx.metadata.reflection

import app.pdfx.metadata.MetadataField
import app.pdfx.metadata.MetadataFieldName
import app.pdfx.metadata.MetadataFieldType
import app.pdfx.metadata.MetadataInfo
import app.pdfx.metadata.annotation.FieldId
import app.pdfx.metadata.annotation.MdStruct
import java.time.Instant
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass

/**
 * Creates a structure map of the PDF metadata during compilation.
 */
class AnnotationProcessor : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processingEnv.messager.printNote(">>> START\r\n")
        doProcess(roundEnv)
        processingEnv.messager.printNote(">>> END\r\n")
        return true
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(/*MdStruct::class.qualifiedName!!*/"app.pdfx.metadata.annotation.MdStruct")
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedOptions(): Set<String> {
        return emptySet()
    }

    private fun doProcess(roundEnv: RoundEnvironment) {
        val fieldsByName: MutableMap<String, List<MetadataField>> = LinkedHashMap()
        val enabledFieldsByName: MutableMap<String, List<MetadataField>> = LinkedHashMap()

        val root: TypeElement = roundEnv.getElementsAnnotatedWith(MdStruct::class.java).first {
            it is TypeElement
                    && it.getAnnotation(MdStruct::class.java).type == MdStruct.Type.ROOT_STRUCT
                    && it.simpleName.contentEquals(MetadataInfo::class.simpleName)
        } as TypeElement

        processingEnv.messager.printNote(">>> PROCESSING ${root.qualifiedName}\r\n", root)

        traverseFields(
            emptyList(),
            false,
            root,
            MdStruct.Type.CHILD_STRUCT
        ) { fieldDescs: List<MetadataField> ->
            if (fieldDescs.isNotEmpty()) {
                fieldsByName[fieldDescs[fieldDescs.size - 1].name.qualifiedName] = fieldDescs
            }
        }
        traverseFields(
            emptyList(),
            false,
            root,
            MdStruct.Type.CHILD_ENABLE_STRUCT
        ) { fieldDescs: List<MetadataField> ->
            if (fieldDescs.isNotEmpty()) {
                enabledFieldsByName[fieldDescs[fieldDescs.size - 1].name.qualifiedName] = fieldDescs
            }
        }

        MetadataInfo._mdFields = fieldsByName
        MetadataInfo._mdEnabledFields = enabledFieldsByName
    }

    private fun traverseFields(
        ancestors: List<MetadataField>,
        all: Boolean,
        klass: TypeElement,
        mdType: MdStruct.Type,
        consumer: (List<MetadataField>) -> Unit
    ) {
        val fields: List<VariableElement> = klass.enclosedElements
            .filter { it is VariableElement && it.kind == ElementKind.FIELD }
            .map { it as VariableElement }

        processingEnv.messager.printNote(">>> TRAVERSING ${klass.qualifiedName}\r\n", klass)
        processingEnv.messager.printNote(">>> TRAVERSING BY ${fields.joinToString("\r\n") { it.simpleName }}\r\n")

        for (field in fields) {
            val mdStruct = field.getAnnotation(MdStruct::class.java)
            val fieldType: TypeMirror = field.asType()

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
                    val prefix = if (ancestors.isNotEmpty()) "${ancestors[ancestors.size - 1].name.qualifiedName}." else ""
                    val t = createField(prefix + fieldId.value, fieldType, fieldId.type, isParentWritable)
                    val a: MutableList<MetadataField> = ancestors.toMutableList()
                    a.add(t)
                    consumer(a)
                } else if (all) {
                    val prefix = if (ancestors.isNotEmpty()) "${ancestors[ancestors.size - 1].name.qualifiedName}." else ""
                    val t = createField(prefix + field.simpleName, fieldType, isParentWritable)
                    consumer(ancestors + t)
                }
            }
        }
    }

    private fun createField(qualifiedName: String, field: TypeMirror, type: MetadataFieldType?, writable: Boolean): MetadataField {
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
                typeUtils.isAssignable(field, elementUtils.getTypeElement(List::class.qualifiedName).asType())
            }
        )
    }

    private fun isAssignable(first: TypeMirror, second: KClass<*>): Boolean {
        return processingEnv.run {
            typeUtils.isAssignable(first, elementUtils.getTypeElement(second.qualifiedName).asType())
        }
    }
}
