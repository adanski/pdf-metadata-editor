package io.pdfx.desktop.view.tab.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

class XmpRightsState {

    private val titleState = remember { mutableStateOf(TextFieldValue()) }
    private val markedState = remember { mutableStateOf("") }
    private val ownersState = remember { mutableStateOf(TextFieldValue()) }
    private val usageTermsState = remember { mutableStateOf(TextFieldValue()) }
    private val webStatementState = remember { mutableStateOf(TextFieldValue()) }

    var title: TextFieldValue
        get() = titleState.value
        set(value) {
            titleState.value = value
        }
    var marked: String
        get() = markedState.value
        set(value) {
            markedState.value = value
        }
    var owners: TextFieldValue
        get() = ownersState.value
        set(value) {
            ownersState.value = value
        }
    var usageTerms: TextFieldValue
        get() = usageTermsState.value
        set(value) {
            usageTermsState.value = value
        }
    var webStatement: TextFieldValue
        get() = webStatementState.value
        set(value) {
            webStatementState.value = value
        }

}
