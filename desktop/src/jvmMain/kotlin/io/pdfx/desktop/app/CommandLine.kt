package io.pdfx.desktop.app

import io.pdfx.desktop.app.metadata.MetadataInfo
import io.pdfx.desktop.util.ParseError
import java.util.*

class CommandLine {

    @JvmField
    var fileList: MutableList<String> = ArrayList()
    @JvmField
    var noGui = System.getProperty("noGui") != null
    @JvmField
    var command: CommandDescription? = null
    @JvmField
    var params = io.pdfx.desktop.app.BatchOperationParameters()
    @JvmField
    var batchGui = false
    @JvmField
    var showHelp = false

    constructor()
    constructor(fileList: MutableList<String>) {
        this.fileList = fileList
    }

    constructor(fileList: MutableList<String>, batchGui: Boolean) {
        this.fileList = fileList
        this.batchGui = batchGui
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("CommandLine(")
        sb.append("noGui=")
        sb.append(noGui)
        sb.append(", ")
        sb.append("batchGui=")
        sb.append(batchGui)
        sb.append(", ")
        sb.append("showHelp=")
        sb.append(showHelp)
        sb.append(", ")
        sb.append("command=")
        sb.append(if (command != null) command!!.name else "")
        sb.append(", ")
        sb.append("files=[")
        val it: Iterator<String> = fileList.iterator()
        while (it.hasNext()) {
            sb.append(it.next())
            if (it.hasNext()) {
                sb.append(", ")
            }
        }
        sb.append("])")
        return sb.toString()
    }

    fun hasCommand(): Boolean {
        return command != null
    }

    companion object {

        fun mdFieldsHelpMessage(lineLen: Int, markReadOnly: Boolean): String {
            return mdFieldsHelpMessage(lineLen, "  ", "", markReadOnly)
        }

        fun mdFieldsHelpMessage(lineLen: Int, pre: String, post: String, markReadOnly: Boolean): String {
            var maxLen = 0
            for (s in validMdNames) {
                val additionalLength = if (markReadOnly && !MetadataInfo.keyIsWritable(s)) 1 else 0
                if (s.length + additionalLength > maxLen) {
                    maxLen = s.length + post.length + additionalLength
                }
            }
            var ll = 0
            val sb = StringBuilder()
            for (s in validMdNames) {
                sb.append(pre)
                sb.append(
                    String.format(
                        "%1$-" + maxLen + "s",
                        s + (if (markReadOnly && !MetadataInfo.keyIsWritable(s)) "*" else "") + post
                    )
                )
                ll += maxLen + pre.length
                if (ll >= lineLen) {
                    sb.append('\n')
                    ll = 0
                }
            }
            if (ll != 0) {
                sb.append('\n')
            }
            return sb.toString()
        }

        var validMdNames: Set<String> = LinkedHashSet(MetadataInfo.keys())

        private fun processOptions(startIndex: Int, args: List<String>, cmdLine: CommandLine): Int {
            var i = startIndex
            while (i < args.size && args[i].startsWith("-")) {
                val arg = if (args[i].startsWith("--")) args[i].substring(2) else args[i].substring(1)
                if (arg.equals("nogui", ignoreCase = true) || arg.equals("console", ignoreCase = true)) {
                    cmdLine.noGui = true
                } else if (arg.equals("rt", ignoreCase = true) || arg.equals("renameTemplate", ignoreCase = true)) {
                    if (i + 1 < args.size) {
                        cmdLine.params.renameTemplate = args[i + 1]
                        ++i
                    } else {
                        throw ParseError("Missing argument for renameTemplate")
                    }
                } else if (arg.equals("h", ignoreCase = true) || arg.equals("help", ignoreCase = true)) {
                    cmdLine.showHelp = true
                } else {
                    throw ParseError("Invalid option: $arg")
                }
                ++i
            }
            return i
        }


        fun parse(args: Array<String>): CommandLine {
            return parse(listOf(*args))
        }

        fun parse(args: List<String>): CommandLine {
            val cmdLine = CommandLine()
            cmdLine.params.metadata.setEnabled(false)
            var i = processOptions(0, args, cmdLine)
            if (i < args.size) {
                cmdLine.command = CommandDescription.getBatchCommand(args[i])
                if (cmdLine.command != null) {
                    ++i
                } else if (args[i].matches("^batch-gui-\\w+$".toRegex())) {
                    cmdLine.batchGui = true
                    ++i
                }
            }
            while (i < args.size) {
                var arg = args[i]
                var enable = true
                if (arg[0] == '!') {
                    enable = false
                    arg = arg.substring(1)
                }
                val eqIndex = arg.indexOf("=")
                if (eqIndex >= 0) {
                    val id = arg.substring(0, eqIndex)
                    if (validMdNames.contains(id)) {
                        val value = arg.substring(eqIndex + 1).trim { it <= ' ' }
                        cmdLine.params.metadata.setAppendFromString(id, value)
                        cmdLine.params.metadata.setEnabled(id, enable)
                    }
                } else if (validMdNames.contains(arg)) {
                    cmdLine.params.metadata.setEnabled(arg, enable)
                } else if (arg == "all") {
                    cmdLine.params.metadata.setEnabled(true)
                } else if (arg == "none") {
                    cmdLine.params.metadata.setEnabled(false)
                } else if (args[i] == "--") {
                    ++i
                    break
                } else {
                    break
                }
                ++i
            }
            while (i < args.size) {
                cmdLine.fileList.add(args[i])
                ++i
            }
            return cmdLine
        }
    }
}
