package io.pdfx.desktop.app

import io.pdfx.app.CsvMetadata.readFile
import io.pdfx.app.metadata.MetadataInfo
import java.io.*
import java.nio.file.Files
import java.util.*

class PdfMetadataEditBatch @JvmOverloads constructor(var params: io.pdfx.desktop.app.BatchOperationParameters? = null) {
    interface ActionStatus {
        fun addStatus(filename: String, message: String)
        fun addError(filename: String, error: String)
    }

    interface FileAction {
        fun apply(file: File)
        fun ignore(file: File)
    }

    fun forFiles(file: File, filter: FileFilter?, action: FileAction) {
        if (file.isFile) {
            if (isPdfExtension(file)) {
                action.apply(file)
            } else {
                action.ignore(file)
            }
        } else if (file.isDirectory) {
            for (f in file.listFiles(filter)) {
                action.apply(f)
            }
        } else {
            action.ignore(file)
        }
    }

    fun forFiles(files: List<File>, filter: FileFilter?, action: FileAction) {
        for (file in files) {
            forFiles(file, filter, action)
        }
    }

    protected var defaultFileFilter = FileFilter { file: File -> isPdfExtension(file) }
    fun forFiles(file: File, action: FileAction) {
        forFiles(file, defaultFileFilter, action)
    }

    fun forFiles(files: List<File>, action: FileAction) {
        forFiles(files, defaultFileFilter, action)
    }

    fun edit(files: List<File>, status: ActionStatus) {
        if (params == null) {
            status.addError("*", "No metadata defined")
            return
        }
        forFiles(files, object : FileAction {
            override fun apply(file: File) {
                val mdParams = if (params != null) params!!.metadata else MetadataInfo()
                try {
                    val mdFile = MetadataInfo()
                    mdFile.loadFromPDF(file)
                    val md = mdParams.clone()
                    md.expand(mdFile)
                    md.saveAsPDF(file)
                    status.addStatus(file.name, "Done")
                } catch (e: Exception) {
                    e.printStackTrace()
                    status.addError(file.name, "Failed: $e")
                }
            }

            override fun ignore(file: File) {
                status.addError(file.name, "Invalid file:" + file.absolutePath)
            }
        })
    }

    fun clear(files: List<File>, status: ActionStatus) {
        forFiles(files, object : FileAction {
            override fun apply(file: File) {
                val md = if (params != null) params!!.metadata else MetadataInfo()
                try {
                    md.saveAsPDF(file)
                    status.addStatus(file.name, "Cleared")
                } catch (e: Exception) {
                    e.printStackTrace()
                    status.addError(file.name, "Failed: $e")
                }
            }

            override fun ignore(file: File) {
                status.addError(file.name, "Invalid file:" + file.absolutePath)
            }
        })
    }

    fun rename(files: List<File>, status: ActionStatus) {
        var template: String? = null
        if (params != null) {
            template = params!!.renameTemplate
            if (!template!!.lowercase(Locale.getDefault()).endsWith(".pdf")) template += ".pdf"
        }
        if (template == null) {
            status.addError("*", "Rename template not configured")
            return
        }
        val ts = TemplateString(template)
        forFiles(files, object : FileAction {
            override fun apply(file: File) {
                try {
                    val md = MetadataInfo()
                    md.loadFromPDF(file)
                    val toName = ts.process(md)
                    val toDir = file.parent
                    val to = File(toDir, toName)
                    if (to.exists()) {
                        status.addError(file.name, "Destination file already exists:  " + to.name)
                    } else {
                        try {
                            Files.move(file.toPath(), to.toPath())
                            status.addStatus(file.name, to.name)
                        } catch (e: IOException) {
                            status.addError(file.name, "Rename failed with " + to.name + " : " + e)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    status.addError(file.name, "Failed: $e")
                }
            }

            override fun ignore(file: File) {
                status.addError(file.name, "Invalid file:" + file.absolutePath)
            }
        })
    }

    fun tojson(files: List<File>, status: ActionStatus) {
        forFiles(files, object : FileAction {
            override fun apply(file: File) {
                try {
                    val md = MetadataInfo()
                    md.loadFromPDF(file)
                    var outFile = file.absolutePath.replaceFirst("\\.[Pp][Dd][Ff]$".toRegex(), ".json")
                    if (!outFile.endsWith(".json")) {
                        outFile = file.absolutePath + ".json"
                    }
                    val out: Writer = BufferedWriter(
                        OutputStreamWriter(
                            FileOutputStream(outFile), "UTF8"
                        )
                    )
                    out.write(md.toJson(true))
                    out.close()
                    status.addStatus(file.name, outFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    status.addError(file.name, "Failed: $e")
                }
            }

            override fun ignore(file: File) {
                status.addError(file.name, "Invalid file:" + file.absolutePath)
            }
        })
    }

    fun toyaml(files: List<File>, status: ActionStatus) {
        forFiles(files, object : FileAction {
            override fun apply(file: File) {
                try {
                    val md = MetadataInfo()
                    md.loadFromPDF(file)
                    var outFile = file.absolutePath.replaceFirst("\\.[Pp][Dd][Ff]$".toRegex(), ".yaml")
                    if (!outFile.endsWith(".yaml")) {
                        outFile = file.absolutePath + ".yaml"
                    }
                    val out: Writer = BufferedWriter(
                        OutputStreamWriter(
                            FileOutputStream(outFile), "UTF8"
                        )
                    )
                    out.write(md.toYAML(true))
                    out.close()
                    status.addStatus(file.name, outFile)
                } catch (e: Exception) {
                    e.printStackTrace()
                    status.addError(file.name, "Failed: $e")
                }
            }

            override fun ignore(file: File) {
                status.addError(file.name, "Invalid file:" + file.absolutePath)
            }
        })
    }

    private fun fromCsv(csvFiles: List<File>, status: ActionStatus) {
        for (csvFile in csvFiles) {
            try {
                val actionList = readFile(csvFile)
                for (mdParams in actionList) {
                    val file = File(mdParams.file.fullPath)
                    try {
                        val mdFile = MetadataInfo()
                        mdFile.loadFromPDF(file)
                        val md = mdParams.clone()
                        md.expand(mdFile)
                        md.saveAsPDF(file)
                        status.addStatus(file.name, "Done")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        status.addError(file.name, "Failed: $e")
                    }
                }
            } catch (e: Exception) {
                status.addError(csvFile.name, "Failed: $e")
            }
        }
    }

    fun runCommand(command: CommandDescription, batchFileList: List<File>, actionStatus: ActionStatus) {
        if (command.`is`("rename")) {
            rename(batchFileList, actionStatus)
        } else if (command.`is`("edit")) {
            edit(batchFileList, actionStatus)
        } else if (command.`is`("clear")) {
            clear(batchFileList, actionStatus)
        } else if (command.`is`("tojson")) {
            tojson(batchFileList, actionStatus)
        } else if (command.`is`("toyaml")) {
            toyaml(batchFileList, actionStatus)
        } else if (command.`is`("fromcsv")) {
            fromCsv(batchFileList, actionStatus)
        } else {
            actionStatus.addError("*", "Invalid command")
        }
    }

    companion object {
        fun isPdfExtension(file: File): Boolean {
            return file.name.lowercase(Locale.getDefault()).endsWith(".pdf")
        }
    }
}
