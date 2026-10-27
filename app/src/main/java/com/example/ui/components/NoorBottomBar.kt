package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.GoldWarm
import com.example.ui.theme.NightSurface
import com.example.ui.viewmodel.Screen

data class NavigationTabItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val tag: String
)

@Composable
fun NoorBottomBar(
    currentScreen: Screen,
    onTabSelected: (Screen) -> Unit
) {
    val items = listOf(
        NavigationTabItem(Screen.Home, "الرئيسية", Icons.Filled.Home, Icons.Outlined.Home, "nav_home"),
        NavigationTabItem(Screen.Mushaf, "المصحف", Icons.Filled.MenuBook, Icons.Outlined.MenuBook, "nav_mushaf"),
        NavigationTabItem(Screen.Index, "الفهرس", Icons.Filled.FormatListNumbered, Icons.Outlined.FormatListNumbered, "nav_index"),
        NavigationTabItem(Screen.Athkar, "الأذكار", Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder, "nav_athkar"),
        NavigationTabItem(Screen.PrayerQibla, "المواقيت", Icons.Filled.Explore, Icons.Outlined.Explore, "nav_prayer")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.testTag("noor_bottom_bar")
    ) {
        items.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(item.screen) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EmeraldDeep,
                    selectedTextColor = EmeraldDeep,
                    indicatorColor = GoldWarm.copy(alpha = 0.35f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag(item.tag)
            )
        }
    }
}
