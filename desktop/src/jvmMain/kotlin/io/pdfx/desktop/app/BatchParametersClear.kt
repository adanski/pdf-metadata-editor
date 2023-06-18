package io.pdfx.desktop.app

import java.awt.Frame

class BatchParametersClear(params: io.pdfx.desktop.app.BatchOperationParameters?, owner: Frame? = null) : BatchParametersEdit(params, owner) {

    init {
        defaultMetadataPane.disableEdit()
        setMessage("Select fields to be cleared below")
    }
}
