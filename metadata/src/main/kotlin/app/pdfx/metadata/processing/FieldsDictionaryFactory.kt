package app.pdfx.metadata.processing

import app.pdfx.metadata.MetadataField
import app.pdfx.metadata.MetadataFieldReference
import app.pdfx.metadata.MetadataFieldType
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

internal class FieldsDictionaryFactory(
    private val processingEnv: ProcessingEnvironment
) {

    fun create(root: TypeElement, vararg specs: () -> PropertySpec): FileSpec {
        val file: FileSpec.Builder = FileSpec.builder(
            "app.pdfx.metadata",
            "${root.simpleName}Fields"
        )

        specs.forEach { file.addProperty(it()) }

        return file.build()
    }

    fun dictionaryField(name: String, fieldsByName: Map<String, List<MetadataField>>): () -> PropertySpec {
        return {
            PropertySpec.builder(name, getMapTypeName())
                .addModifiers(KModifier.PUBLIC)
                .mutable(false)
                .initializer(
                    "mapOf" +
                            "(\n      " +
                            fieldsByName.entries.joinToString(",\n      ") {
                                "Pair(\"${it.key}\", listOf(\n          ${
                                    it.value.joinToString(",\n          ") {
                                        createMetadataField(
                                            metadataField = it
                                        )
                                    }
                                }))"
                            } +
                            ")",
                    MetadataField::class,
                    MetadataFieldReference::class,
                    MetadataFieldType::class
                )
                .build()
        }
    }

    private fun getMapTypeName(): ParameterizedTypeName {
        val map = Map::class.asClassName()
        val list = List::class.parameterizedBy(MetadataField::class)
        return map.parameterizedBy(String::class.asClassName(), list)
    }

    private fun createMetadataField(startingIndex: Int = 1, metadataField: MetadataField): String {
        return "%${startingIndex}T" +
                "(" +
                "%${startingIndex + 1}T(\"${metadataField.reference.name}\", \"${metadataField.reference.referencePath}\", \"${metadataField.reference.ownerQualifiedName}\"), " +
                "${if (metadataField.type == null) "null" else "%${startingIndex + 2}T.${metadataField.type.name}"}, " +
                "${metadataField.list}, " +
                "${metadataField.writable}" +
                ")"
    }
}
