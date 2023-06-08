package app.pdfx

import java.awt.Frame

class BatchParametersClear(params: BatchOperationParameters?, owner: Frame? = null) : BatchParametersEdit(params, owner) {

    init {
        defaultMetadataPane.disableEdit()
        setMessage("Select fields to be cleared below")
    }
}
