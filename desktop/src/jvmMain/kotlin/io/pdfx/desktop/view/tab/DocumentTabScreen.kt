package io.pdfx.desktop.view.tab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.pdfx.common.view.tab.TabScreen
import io.pdfx.desktop.view.tab.model.MetadataViewModel

@Composable
fun DocumentTabScreen(metadataViewModel: MetadataViewModel) {
    TabScreen {
        val document = metadataViewModel.document

        TextField(
            label = {
                Text(text = "Title")
            },
            modifier = Modifier.fillMaxWidth(),
            value = document.title,
            onValueChange = { document.title = it }
        )

        TextField(
            label = {
                Text(text = "Author")
            },
            modifier = Modifier.fillMaxWidth(),
            value = document.author,
            onValueChange = { document.author = it }
        )

        TextField(
            label = {
                Text(text = "Subject")
            },
            modifier = Modifier.fillMaxWidth()
                .height(TextFieldDefaults.MinHeight.times(3)),
            value = document.subject,
            onValueChange = { document.subject = it }
        )

        TextField(
            label = {
                Text(text = "Keywords")
            },
            modifier = Modifier.fillMaxWidth()
                .height(TextFieldDefaults.MinHeight.times(3)),
            value = document.keywords,
            onValueChange = { document.keywords = it }
        )

        TextField(
            label = {
                Text(text = "Creator")
            },
            modifier = Modifier.fillMaxWidth(),
            value = document.creator,
            onValueChange = { document.creator = it }
        )

        TextField(
            label = {
                Text(text = "Producer")
            },
            modifier = Modifier.fillMaxWidth(),
            value = document.producer,
            onValueChange = { document.producer = it }
        )

        Row {
            TextField(
                label = {
                    Text(text = "Creation Date")
                },
                modifier = Modifier.fillMaxWidth(),
                value = document.creationDate,
                isError = document.creationDateValidationResult.error.value,
                onValueChange = { document.creationDate = it }
            )
            Button(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null
                )
            }
        }

        Row {
            TextField(
                label = {
                    Text(text = "Modification Date")
                },
                modifier = Modifier.fillMaxWidth(),
                value = document.modificationDate,
                isError = document.modificationDateValidationResult.error.value,
                onValueChange = { document.modificationDate = it }
            )
            Button(
                onClick = {}
            ) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = null
                )
            }
        }

        val dropdownMenuState = remember { DropdownMenuState() }

        DropdownMenu(
            state = dropdownMenuState
        ) {
            DropdownMenuItem(
                onClick = { document.trapped = "True" }
            ) {
                Text("True")
            }
            DropdownMenuItem(
                onClick = { document.trapped = "False" }
            ) {
                Text("False")
            }
            DropdownMenuItem(
                onClick = { document.trapped = "Unknown" }
            ) {
                Text("Unknown")
            }
        }
    }
}
