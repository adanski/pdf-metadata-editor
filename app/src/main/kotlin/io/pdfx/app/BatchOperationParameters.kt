package io.pdfx.app

import io.pdfx.app.metadata.MetadataInfo

class BatchOperationParameters {

    var metadata = MetadataInfo()
    var renameTemplate: String? = null

    fun storeForCommand(command: CommandDescription) {
        if (command.`is`("edit")) {
            return
        }
        val cmdPrefs = Main.preferences.node("batchParams").node(command.name)
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
                val defaultMetadataYAML = Main.preferences["defaultMetadata", null]
                if (!defaultMetadataYAML.isNullOrEmpty()) {
                    val editMetadata = MetadataInfo()
                    editMetadata.fromYAML(defaultMetadataYAML)
                    editMetadata.enableOnlyNonNull()
                    params.metadata = editMetadata
                }
            } else {
                val cmdPrefs = Main.preferences.node("batchParams").node(command.name)
                val mdP = cmdPrefs["md", null]
                if (mdP != null && mdP.length > 0) {
                    params.metadata = MetadataInfo.fromPersistenceString(mdP)
                }
                params.renameTemplate = cmdPrefs["rt", null]
                if (params.renameTemplate == null && command.`is`("rename")) {
                    params.renameTemplate = Main.preferences["renameTemplate", null]
                }
            }
            return params
        }
    }
}
