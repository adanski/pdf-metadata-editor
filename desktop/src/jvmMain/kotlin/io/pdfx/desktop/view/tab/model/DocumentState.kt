package io.pdfx.desktop.view.tab.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import io.pdfx.desktop.view.model.ValidationResult

class DocumentState {

    private val titleState = remember { mutableStateOf(TextFieldValue()) }
    private val authorState = remember { mutableStateOf(TextFieldValue()) }
    private val subjectState = remember { mutableStateOf(TextFieldValue()) }
    private val keywordsState = remember { mutableStateOf(TextFieldValue()) }
    private val creatorState = remember { mutableStateOf(TextFieldValue()) }
    private val producerState = remember { mutableStateOf(TextFieldValue()) }
    private val creationDateState = remember { mutableStateOf(TextFieldValue()) }
    private val creationDateValidationResultState = remember { mutableStateOf(ValidationResult()) }
    private val modificationDateState = remember { mutableStateOf(TextFieldValue()) }
    private val modificationDateValidationResultState = remember { mutableStateOf(ValidationResult()) }
    private val trappedState = remember { mutableStateOf("") }

    var title: TextFieldValue
        get() = titleState.value
        set(value) {
            titleState.value = value
        }
    var author: TextFieldValue
        get() = authorState.value
        set(value) {
            authorState.value = value
        }
    var subject: TextFieldValue
        get() = subjectState.value
        set(value) {
            subjectState.value = value
        }
    var keywords: TextFieldValue
        get() = keywordsState.value
        set(value) {
            keywordsState.value = value
        }
    var creator: TextFieldValue
        get() = creatorState.value
        set(value) {
            creatorState.value = value
        }
    var producer: TextFieldValue
        get() = producerState.value
        set(value) {
            producerState.value = value
        }
    var creationDate: TextFieldValue
        get() = creationDateState.value
        set(value) {
            creationDateState.value = value
        }
    val creationDateValidationResult: ValidationResult
        get() = creationDateValidationResultState.value
    var modificationDate: TextFieldValue
        get() = modificationDateState.value
        set(value) {
            modificationDateState.value = value
        }
    val modificationDateValidationResult: ValidationResult
        get() = modificationDateValidationResultState.value
    var trapped: String
        get() = trappedState.value
        set(value) {
            trappedState.value = value
        }

}
