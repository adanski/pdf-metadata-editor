package app.pdfx.metadata.annotation

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class MdStruct(
    val name: String = "",
    val type: Type = Type.CHILD_STRUCT,
    val access: Access = Access.READ_WRITE
) {
    enum class Type {
        CHILD_STRUCT,
        CHILD_ENABLE_STRUCT,
        ROOT_STRUCT
    }

    enum class Access {
        READ_ONLY,
        READ_WRITE
    }
}
