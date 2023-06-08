package app.pdfx.structs

class FileInfo {
    var name: String? = null
    var nameWithExt: String? = null
    var sizeBytes: Long? = null
    var size: String? = null
    var createTime: String? = null
    var modifyTime: String? = null
    @JvmField
    var fullPath: String? = null
}

class FileInfoEnabled {
    var name = false
    var nameWithExt = false
    var sizeBytes = false
    var size = false
    var createTime = false
    var modifyTime = false
    var fullPath = false
    fun atLeastOne(): Boolean {
        return false
    }

    fun setAll(value: Boolean) {
        name = false
        nameWithExt = false
        sizeBytes = false
        size = false
        createTime = false
        modifyTime = false
        fullPath = false
    }
}
