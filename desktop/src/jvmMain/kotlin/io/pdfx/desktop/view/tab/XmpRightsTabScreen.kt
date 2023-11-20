package io.pdfx.desktop.view.tab

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import io.pdfx.common.view.tab.TabScreen
import io.pdfx.desktop.view.tab.model.MetadataViewModel

@Composable
fun XmpRightsTabScreen(metadataViewModel: MetadataViewModel) {
    TabScreen {
        Text(text = "XMP Rights Tab")
    }
}
