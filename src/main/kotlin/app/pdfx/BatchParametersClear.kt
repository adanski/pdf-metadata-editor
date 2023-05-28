package app.pdfx

import java.awt.Frame

class BatchParametersClear @JvmOverloads constructor(params: BatchOperationParameters?, owner: Frame? = null) :
    BatchParametersEdit(params, owner) {
    /**
     * @wbp.parser.constructor
     */
    init {
        defaultMetadataPane.disableEdit()
        setMessage("Select fields to be cleared below")
    }
}
