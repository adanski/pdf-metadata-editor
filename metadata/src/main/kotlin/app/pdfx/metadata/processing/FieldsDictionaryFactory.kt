package app.pdfx.metadata.processing

import app.pdfx.metadata.MetadataField
import app.pdfx.metadata.MetadataFieldName
import app.pdfx.metadata.MetadataFieldType
import com.squareup.javapoet.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import java.lang.String as JavaString
import java.util.List as JavaList
import java.util.Map as JavaMap

internal class FieldsDictionaryFactory(
    private val processingEnv: ProcessingEnvironment
) {

    fun create(root: TypeElement, vararg specs: () -> FieldSpec): JavaFile {
        val file: JavaFile = JavaFile.builder(
            "app.pdfx.metadata",
            TypeSpec.classBuilder("${root.simpleName}Fields")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(
                    MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PRIVATE)
                        .build()
                )
                .addFields(specs.map { it() })
                .build()
        )
            .build()

        return file
    }

    fun dictionaryField(name: String, fieldsByName: Map<String, List<MetadataField>>): () -> FieldSpec {
        return {
            FieldSpec.builder(getMapTypeName(), name)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .initializer(
                    "\$1T.ofEntries" +
                            "(\n      " +
                            fieldsByName.entries.joinToString(",\n      ") {
                                "\$1T.entry(\"${it.key}\", \$2T.of(\n          ${
                                    it.value.joinToString(",\n          ") {
                                        createMetadataField(
                                            3,
                                            it
                                        )
                                    }
                                }))"
                            } +
                            ")",
                    JavaMap::class.java,
                    JavaList::class.java,
                    MetadataField::class.java,
                    MetadataFieldName::class.java,
                    MetadataFieldType::class.java
                )
                .build()
        }
    }

    private fun getMapTypeName(): ParameterizedTypeName {
        val map = ClassName.get(JavaMap::class.java)
        val list =
            ParameterizedTypeName.get(ClassName.get(JavaList::class.java), ClassName.get(MetadataField::class.java))
        return ParameterizedTypeName.get(map, ClassName.get(JavaString::class.java), list)
    }

    private fun createMetadataField(startingIndex: Int, metadataField: MetadataField): String {
        return "new \$${startingIndex}T" +
                "(" +
                "new \$${startingIndex + 1}T(\"${metadataField.name.name}\", \"${metadataField.name.qualifiedName}\"), " +
                "${if (metadataField.type == null) "null" else "\$${startingIndex + 2}T.${metadataField.type.name}"}, " +
                "${metadataField.list}, " +
                "${metadataField.writable}" +
                ")"
    }
}
