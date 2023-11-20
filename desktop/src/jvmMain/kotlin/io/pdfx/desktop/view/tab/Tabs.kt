package io.pdfx.desktop.view.tab

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import io.pdfx.common.view.tab.TabItem
import io.pdfx.desktop.view.tab.model.MetadataViewModel

fun tabItems(metadataViewModel: MetadataViewModel) = listOf(
    TabItem(
        title = "Document",
        icon = Icons.Filled.Home
    ) {
        DocumentTabScreen(metadataViewModel)
    },
    TabItem(
        title = "XMP Basic",
        icon = Icons.Filled.Info
    ) {
        XmpBasicTabScreen(metadataViewModel)
    },
    TabItem(
        title = "XMP PDF",
        icon = Icons.Filled.Info
    ) {
        XmpPdfTabScreen(metadataViewModel)
    },
    TabItem(
        title = "XMP Dublin Core",
        icon = Icons.Filled.Info
    ) {
        XmpDublinCoreTabScreen(metadataViewModel)
    },
    TabItem(
        title = "XMP Rights",
        icon = Icons.Filled.Lock
    ) {
        XmpRightsTabScreen(metadataViewModel)
    }
)
