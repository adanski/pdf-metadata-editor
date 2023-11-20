package io.pdfx.desktop.view.tab

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.pdfx.common.view.tab.TabScreen
import io.pdfx.desktop.view.tab.model.MetadataViewModel

@Composable
fun XmpPdfTabScreen(metadataViewModel: MetadataViewModel) {
    TabScreen {
        val xmpPdf = metadataViewModel.xmpPdf

        TextField(
            label = {
                Text(text = "Keywords")
            },
            modifier = Modifier.fillMaxWidth()
                .height(TextFieldDefaults.MinHeight.times(14)),
            value = xmpPdf.keywords,
            onValueChange = { xmpPdf.keywords = it }
        )

        TextField(
            label = {
                Text(text = "PDF Version")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpPdf.pdfVersion,
            onValueChange = { xmpPdf.pdfVersion = it }
        )

        TextField(
            label = {
                Text(text = "Producer")
            },
            modifier = Modifier.fillMaxWidth(),
            value = xmpPdf.producer,
            onValueChange = { xmpPdf.producer = it }
        )
    }
}
