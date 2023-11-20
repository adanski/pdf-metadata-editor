package io.pdfx.desktop.view.tab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.pdfx.common.view.tab.TabScreen
import io.pdfx.desktop.view.tab.model.MetadataViewModel

@Composable
fun XmpBasicTabScreen(metadataViewModel: MetadataViewModel) {
    TabScreen {
        val xmpBasic = metadataViewModel.xmpBasic

        TextField(
            label = {
                Text(text = "Creator Tool")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpBasic.creatorTool,
            onValueChange = { xmpBasic.creatorTool = it }
        )

        Row {
            TextField(
                label = {
                    Text(text = "Creation Date")
                },
                modifier = Modifier.fillMaxWidth(),
                value = xmpBasic.creationDate,
                isError = xmpBasic.creationDateValidationResult.error.value,
                onValueChange = { xmpBasic.creationDate = it }
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
                value = xmpBasic.modificationDate,
                isError = xmpBasic.modificationDateValidationResult.error.value,
                onValueChange = { xmpBasic.modificationDate = it }
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

        TextField(
            label = {
                Text(text = "Base URL")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpBasic.baseUrl,
            onValueChange = { xmpBasic.baseUrl = it }
        )

        TextField(
            label = {
                Text(text = "Rating")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpBasic.rating,
            onValueChange = { xmpBasic.rating = it }
        )

        TextField(
            label = {
                Text(text = "Label")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpBasic.label,
            onValueChange = { xmpBasic.label = it }
        )

        TextField(
            label = {
                Text(text = "Nickname")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpBasic.nickname,
            onValueChange = { xmpBasic.nickname = it }
        )

        TextField(
            label = {
                Text(text = "Identifiers")
            },
            modifier = Modifier.fillMaxWidth()
                .height(TextFieldDefaults.MinHeight.times(3)),
            value = xmpBasic.identifiers,
            onValueChange = { xmpBasic.identifiers = it }
        )

        TextField(
            label = {
                Text(text = "Advisories")
            },
            modifier = Modifier.fillMaxWidth()
                .height(TextFieldDefaults.MinHeight.times(3)),
            value = xmpBasic.advisories,
            onValueChange = { xmpBasic.advisories = it }
        )

        Row {
            TextField(
                label = {
                    Text(text = "Metadata Date")
                },
                modifier = Modifier.fillMaxWidth(),
                value = xmpBasic.metadataDate,
                isError = xmpBasic.metadataDateValidationResult.error.value,
                onValueChange = { xmpBasic.metadataDate = it }
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

    }
}
