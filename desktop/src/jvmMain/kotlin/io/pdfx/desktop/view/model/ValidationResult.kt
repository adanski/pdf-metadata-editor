package io.pdfx.desktop.view.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

data class ValidationResult(
    val error: MutableState<Boolean> = remember { mutableStateOf(false) },
    val message: MutableState<String> = remember { mutableStateOf("") },
)
