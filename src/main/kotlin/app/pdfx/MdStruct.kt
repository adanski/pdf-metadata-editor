package app.pdfx

@Retention(AnnotationRetention.RUNTIME)
annotation class MdStruct(
    val name: String = "",
    val type: StructType = StructType.MD_STRUCT,
    val access: Access = Access.READ_WRITE
) {
    enum class StructType {
        MD_STRUCT,
        MD_ENABLE_STRUCT
    }

    enum class Access {
        READ_ONLY,
        READ_WRITE
    }
}
