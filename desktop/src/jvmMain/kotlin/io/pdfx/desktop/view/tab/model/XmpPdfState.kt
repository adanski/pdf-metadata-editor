package io.pdfx.desktop.view.tab.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

class XmpPdfState {

    private val keywordsState = remember { mutableStateOf(TextFieldValue()) }
    private val pdfVersionState = remember { mutableStateOf(TextFieldValue()) }
    private val producerState = remember { mutableStateOf(TextFieldValue()) }

    var keywords: TextFieldValue
        get() = keywordsState.value
        set(value) {
            keywordsState.value = value
        }
    var pdfVersion: TextFieldValue
        get() = pdfVersionState.value
        set(value) {
            pdfVersionState.value = value
        }
    var producer: TextFieldValue
        get() = producerState.value
        set(value) {
            producerState.value = value
        }

}
