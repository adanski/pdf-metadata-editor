package io.pdfx.desktop

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Place
import io.pdfx.desktop.tab.TabItem
import io.pdfx.desktop.tab.TabScreen

val TABS = listOf(
    TabItem(
        title = "Account",
        icon = Icons.Filled.AccountBox,
        screen = {
            TabScreen(content = "Account Page")
        }
    ),
    TabItem(
        title = "Favorite",
        icon = Icons.Filled.Favorite,
        screen = {
            TabScreen(content = "Favorite list")
        }
    ),
    TabItem(
        title = "Place",
        icon = Icons.Filled.Place,
        screen = {
            TabScreen(content = "Places")
        }
    )
)
