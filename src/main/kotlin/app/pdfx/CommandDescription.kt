package app.pdfx

class CommandDescription protected constructor(@JvmField var name: String, var description: String) {
    var regKey: String
    override fun toString(): String {
        return description
    }

    fun `is`(command: String): Boolean {
        return description == command
    }

    init {
        regKey = "pme." + regKeyCount++ + description
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
                sb.append(String.format("%1$-" + descriptionOffset + "s", cd.description))
                sb.append(cd.description)
                sb.append('\n')
            }
            return sb.toString()
        }

        fun getBatchCommand(command: String): CommandDescription? {
            for (c in batchCommands) {
                if (c.description == command) {
                    return c
                }
            }
            return null
        }
    }
}