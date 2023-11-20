package io.pdfx.desktop.view.tab.model

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.TextFieldValue

class XmpDublinCoreState {

    private val titleState = remember { mutableStateOf(TextFieldValue()) }
    private val descriptionState = remember { mutableStateOf(TextFieldValue()) }
    private val creatorsState = remember { mutableStateOf(TextFieldValue()) }
    private val contributorsState = remember { mutableStateOf(TextFieldValue()) }
    private val coverageState = remember { mutableStateOf(TextFieldValue()) }
    private val datesState = remember { mutableStateOf(TextFieldValue()) }
    private val formatState = remember { mutableStateOf(TextFieldValue()) }
    private val identifierState = remember { mutableStateOf(TextFieldValue()) }
    private val languagesState = remember { mutableStateOf(TextFieldValue()) }
    private val publishersState = remember { mutableStateOf(TextFieldValue()) }
    private val relationshipsState = remember { mutableStateOf(TextFieldValue()) }
    private val rightsState = remember { mutableStateOf(TextFieldValue()) }
    private val sourceState = remember { mutableStateOf(TextFieldValue()) }
    private val subjectsState = remember { mutableStateOf(TextFieldValue()) }
    private val typesState = remember { mutableStateOf(TextFieldValue()) }

    var title: TextFieldValue
        get() = titleState.value
        set(value) {
            titleState.value = value
        }
    var description: TextFieldValue
        get() = descriptionState.value
        set(value) {
            descriptionState.value = value
        }
    var creators: TextFieldValue
        get() = creatorsState.value
        set(value) {
            creatorsState.value = value
        }
    var contributors: TextFieldValue
        get() = contributorsState.value
        set(value) {
            contributorsState.value = value
        }
    var coverage: TextFieldValue
        get() = coverageState.value
        set(value) {
            coverageState.value = value
        }
    var dates: TextFieldValue
        get() = datesState.value
        set(value) {
            datesState.value = value
        }
    var format: TextFieldValue
        get() = formatState.value
        set(value) {
            formatState.value = value
        }
    var identifier: TextFieldValue
        get() = identifierState.value
        set(value) {
            identifierState.value = value
        }
    var languages: TextFieldValue
        get() = languagesState.value
        set(value) {
            languagesState.value = value
        }
    var publishers: TextFieldValue
        get() = publishersState.value
        set(value) {
            publishersState.value = value
        }
    var relationships: TextFieldValue
        get() = relationshipsState.value
        set(value) {
            relationshipsState.value = value
        }
    var rights: TextFieldValue
        get() = rightsState.value
        set(value) {
            rightsState.value = value
        }
    var source: TextFieldValue
        get() = sourceState.value
        set(value) {
            sourceState.value = value
        }
    var subjects: TextFieldValue
        get() = subjectsState.value
        set(value) {
            subjectsState.value = value
        }
    var types: TextFieldValue
        get() = typesState.value
        set(value) {
            typesState.value = value
        }

}
