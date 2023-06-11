package io.pdfx.app

class CommandDescription private constructor(val name: String, val description: String) {
    var regKey: String = "pme." + regKeyCount++ + description
    override fun toString(): String {
        return description
    }

    fun `is`(command: String): Boolean {
        return name == command
    }

    companion object {
        protected var regKeyCount = 1
        val batchCommands = arrayOf(
            CommandDescription("edit", "Set metadata"),
            CommandDescription("clear", "Clear metadata"),
            CommandDescription("rename", "Rename files from metadata"),
            CommandDescription("tojson", "Extract metadata as JSON"),
            CommandDescription("toyaml", "Extract metadata as YAML/Text"),
            CommandDescription("fromcsv", "Set metadata from CSV file")
        )

        @JvmStatic
        fun helpMessage(descriptionOffset: Int): String {
            val sb = StringBuilder()
            for (cd in batchCommands) {
                sb.append("  ")
                sb.append(String.format("%1$-" + descriptionOffset + "s", cd.name))
                sb.append(cd.description)
                sb.append('\n')
            }
            return sb.toString()
        }

        fun getBatchCommand(command: String): CommandDescription? {
            for (c in batchCommands) {
                if (c.name == command) {
                    return c
                }
            }
            return null
        }
    }
}
