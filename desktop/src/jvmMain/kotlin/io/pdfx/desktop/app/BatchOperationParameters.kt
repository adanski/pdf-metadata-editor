package io.pdfx.desktop.app

import io.pdfx.desktop.app.metadata.MetadataInfo
import io.pdfx.common.prefs.APP_PREFERENCES

class BatchOperationParameters {

    var metadata = MetadataInfo()
    var renameTemplate: String? = null

    fun storeForCommand(command: CommandDescription) {
        if (command.`is`("edit")) {
            return
        }
        val cmdPrefs = APP_PREFERENCES.node("batchParams").node(command.name)
        cmdPrefs.putString("md", metadata.asPersistenceString())
        if (renameTemplate != null) {
            cmdPrefs.putString("rt", renameTemplate!!)
        }
    }

    companion object {
        @JvmStatic
        fun loadForCommand(command: CommandDescription): BatchOperationParameters {
            val params = BatchOperationParameters()
            if (command.`is`("edit")) {
                val defaultMetadataYAML = APP_PREFERENCES.getString("defaultMetadata")
                if (!defaultMetadataYAML.isNullOrEmpty()) {
                    val editMetadata = MetadataInfo()
                    editMetadata.fromYAML(defaultMetadataYAML)
                    editMetadata.enableOnlyNonNull()
                    params.metadata = editMetadata
                }
            } else {
                val cmdPrefs = APP_PREFERENCES.node("batchParams").node(command.name)
                val mdP = cmdPrefs.getString("md")
                if (mdP != null && mdP.length > 0) {
                    params.metadata = MetadataInfo.fromPersistenceString(mdP)
                }
                params.renameTemplate = cmdPrefs.getString("rt")
                if (params.renameTemplate == null && command.`is`("rename")) {
                    params.renameTemplate = APP_PREFERENCES.getString("renameTemplate")
                }
            }
            return params
        }
    }
}
