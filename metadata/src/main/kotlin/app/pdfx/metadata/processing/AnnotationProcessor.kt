package app.pdfx.metadata.processing

import app.pdfx.metadata.MetadataField
import app.pdfx.metadata.annotation.MdStruct
import com.squareup.kotlinpoet.FileSpec
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import kotlin.collections.set

/**
 * Creates a structure map of the PDF metadata during compilation.
 */
internal class AnnotationProcessor : AbstractProcessor() {

    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        processingEnv.messager.printNote("${this::class.qualifiedName} START\r\n")
        doProcess(roundEnv)
        processingEnv.messager.printNote("${this::class.qualifiedName} END\r\n")
        return true
    }

    override fun getSupportedAnnotationTypes(): Set<String> {
        return setOf(MdStruct::class.qualifiedName!!)
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

        val root: TypeElement = roundEnv.getElementsAnnotatedWith(MdStruct::class.java).firstOrNull {
            it is TypeElement
                    && it.getAnnotation(MdStruct::class.java).type == MdStruct.Type.ROOT_STRUCT
        } as TypeElement? ?: return

        processingEnv.messager.printNote("PROCESSING ${root.qualifiedName}\r\n", root)

        val traverser: FieldTraverser = FieldTraverser(processingEnv)

        traverser.traverseFields(
            emptyList(),
            false,
            root,
            MdStruct.Type.CHILD_STRUCT
        ) { fieldDescs: List<MetadataField> ->
            if (fieldDescs.isNotEmpty()) {
                fieldsByName[fieldDescs[fieldDescs.size - 1].name.qualifiedName] = fieldDescs
            }
        }
        traverser.traverseFields(
            emptyList(),
            false,
            root,
            MdStruct.Type.CHILD_ENABLE_STRUCT
        ) { fieldDescs: List<MetadataField> ->
            if (fieldDescs.isNotEmpty()) {
                enabledFieldsByName[fieldDescs[fieldDescs.size - 1].name.qualifiedName] = fieldDescs
            }
        }

        val fieldsDictionaryFactory = FieldsDictionaryFactory(processingEnv)
        val file: FileSpec = fieldsDictionaryFactory.create(
            root,
            fieldsDictionaryFactory.dictionaryField("FIELDS_BY_NAME", fieldsByName),
            fieldsDictionaryFactory.dictionaryField("ENABLED_FIELDS_BY_NAME", enabledFieldsByName)
        )

        file.writeTo(processingEnv.filer)
    }
}
