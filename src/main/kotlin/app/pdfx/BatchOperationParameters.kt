package app.pdfx

class BatchOperationParameters {
    @JvmField
    var metadata = MetadataInfo()
    @JvmField
    var renameTemplate: String? = null
    fun storeForCommand(command: CommandDescription) {
        if (command.`is`("edit")) {
            return
        }
        val cmdPrefs = Main.getPreferences().node("batchParams").node(command.name)
        cmdPrefs.put("md", metadata.asPersistenceString())
        if (renameTemplate != null) {
            cmdPrefs.put("rt", renameTemplate)
        }
    }

    companion object {
        @JvmStatic
        fun loadForCommand(command: CommandDescription): BatchOperationParameters {
            val params = BatchOperationParameters()
            if (command.`is`("edit")) {
                val defaultMetadataYAML = Main.getPreferences()["defaultMetadata", null]
                if (defaultMetadataYAML != null && defaultMetadataYAML.length > 0) {
                    val editMetadata = MetadataInfo()
                    editMetadata.fromYAML(defaultMetadataYAML)
                    editMetadata.enableOnlyNonNull()
                    params.metadata = editMetadata
                }
            } else {
                val cmdPrefs = Main.getPreferences().node("batchParams").node(command.name)
                val mdP = cmdPrefs["md", null]
                if (mdP != null && mdP.length > 0) {
                    params.metadata = MetadataInfo.fromPersistenceString(mdP)
                }
                params.renameTemplate = cmdPrefs["rt", null]
                if (params.renameTemplate == null && command.`is`("rename")) {
                    params.renameTemplate = Main.getPreferences()["renameTemplate", null]
                }
            }
            return params
        }
    }
}
