package io.pdfx.desktop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import io.pdfx.desktop.view.tab.model.MetadataViewModel
import io.pdfx.desktop.view.tab.tabItems
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
fun main() {
    application(exitProcessOnExit = true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "PDFx Metadata Editor",
            icon = painterResource("icon-lin.png"),
            state = WindowState(size = DpSize(1440.dp, 768.dp))
        ) {
            val pagerState = rememberPagerState()
            val metadataViewModel = MetadataViewModel()
            val tabItems = tabItems(metadataViewModel)
            val coroutineScope = rememberCoroutineScope()
            MaterialTheme(colors = if (isSystemInDarkTheme()) darkColors() else lightColors()) {
                Column {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage,
                    ) {
                        tabItems.forEachIndexed { index, item ->
                            LeadingIconTab(
                                selected = index == pagerState.currentPage,
                                text = { Text(text = item.title) },
                                icon = { Icon(item.icon, "") },
                                onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } }
                            )
                        }
                    }

                    HorizontalPager(
                        pageCount = tabItems.size,
                        state = pagerState
                    ) {
                        tabItems[pagerState.currentPage].screen()
                    }
                }
            }


        }
    }
}
