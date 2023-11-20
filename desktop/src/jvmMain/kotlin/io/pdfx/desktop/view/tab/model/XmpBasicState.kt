package io.pdfx.desktop.view.tab.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue
import io.pdfx.desktop.view.model.ValidationResult

class XmpBasicState {

    private val creatorToolState = remember { mutableStateOf(TextFieldValue()) }
    private val creationDateState = remember { mutableStateOf(TextFieldValue()) }
    private val creationDateValidationResultState = remember { mutableStateOf(ValidationResult()) }
    private val modificationDateState = remember { mutableStateOf(TextFieldValue()) }
    private val modificationDateValidationResultState = remember { mutableStateOf(ValidationResult()) }
    private val baseUrlState = remember { mutableStateOf(TextFieldValue()) }
    private val ratingState = remember { mutableStateOf(TextFieldValue()) }
    private val labelState = remember { mutableStateOf(TextFieldValue()) }
    private val nicknameState = remember { mutableStateOf(TextFieldValue()) }
    private val identifiersState = remember { mutableStateOf(TextFieldValue()) }
    private val advisoriesState = remember { mutableStateOf(TextFieldValue()) }
    private val metadataDateState = remember { mutableStateOf(TextFieldValue()) }
    private val metadataDateValidationResultState = remember { mutableStateOf(ValidationResult()) }

    var creatorTool: TextFieldValue
        get() = creatorToolState.value
        set(value) {
            creatorToolState.value = value
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
    var baseUrl: TextFieldValue
        get() = baseUrlState.value
        set(value) {
            baseUrlState.value = value
        }
    var rating: TextFieldValue
        get() = ratingState.value
        set(value) {
            ratingState.value = value
        }
    var label: TextFieldValue
        get() = labelState.value
        set(value) {
            labelState.value = value
        }
    var nickname: TextFieldValue
        get() = nicknameState.value
        set(value) {
            nicknameState.value = value
        }
    var identifiers: TextFieldValue
        get() = identifiersState.value
        set(value) {
            identifiersState.value = value
        }
    var advisories: TextFieldValue
        get() = advisoriesState.value
        set(value) {
            advisoriesState.value = value
        }
    var metadataDate: TextFieldValue
        get() = metadataDateState.value
        set(value) {
            metadataDateState.value = value
        }
    val metadataDateValidationResult: ValidationResult
        get() = metadataDateValidationResultState.value

}
