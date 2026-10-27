package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.ui.components.AyahContextMenuSheet
import com.example.ui.components.MiniPlayerBar
import com.example.ui.components.NoorBottomBar
import com.example.ui.components.NoorTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    private val viewModel: NoorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // Enforce genuine Arabic Right-to-Left layout
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    NoorMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun NoorMainApp(viewModel: NoorViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val audioState by viewModel.audioState.collectAsState()
    val contextMenuAyah by viewModel.contextMenuAyah.collectAsState()

    // Handle back button from any sub-screen back to Home
    BackHandler(enabled = currentScreen != Screen.Home) {
        viewModel.navigateTo(Screen.Home)
    }

    Scaffold(
        topBar = {
            NoorTopBar(
                title = when (currentScreen) {
                    is Screen.Home -> "نور — NOOR"
                    is Screen.Mushaf -> "المصحف الشريف"
                    is Screen.Index -> "فهرس القرآن الكريم"
                    is Screen.Search -> "البحث القرآني"
                    is Screen.AudioPlayer -> "مشغل التلاوات"
                    is Screen.Tafsir -> "التفسير والتدبر"
                    is Screen.Memorization -> "الحفظ والمراجعة"
                    is Screen.Athkar -> "الأذكار والأدعية"
                    is Screen.Tasbih -> "السبحة الرقمية"
                    is Screen.PrayerQibla -> "المواقيت وبوصلة القبلة"
                    is Screen.Settings -> "الإعدادات"
                    is Screen.Downloads -> "إدارة التنزيلات"
                    is Screen.Profile -> "الملف الشخصي"
                    is Screen.AiTutor -> "معلم القرآن بالذكاء الاصطناعي"
                    is Screen.RecitationPractice -> "التسميع والتعرف على التلاوة"
                    is Screen.Topics -> "موضوعات القرآن الكريم"
                    is Screen.Collections -> "مجموعاتي المختارة"
                    is Screen.Khatma -> "ختمات القرآن الكريم"
                },
                subtitle = if (currentScreen == Screen.Home) "القرآن الكريم والتلاوات والتدبر" else null,
                showBackButton = currentScreen != Screen.Home,
                onBackClick = { viewModel.navigateTo(Screen.Home) },
                actions = {
                    if (currentScreen != Screen.Search) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Search) },
                            modifier = Modifier.testTag("top_action_search")
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "بحث", tint = PureWhite)
                        }
                    }
                    if (currentScreen != Screen.Settings) {
                        IconButton(
                            onClick = { viewModel.navigateTo(Screen.Settings) },
                            modifier = Modifier.testTag("top_action_settings")
                        ) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "الإعدادات", tint = PureWhite)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                // Mini audio player bar docked above bottom nav
                if (currentScreen != Screen.AudioPlayer) {
                    MiniPlayerBar(
                        audioState = audioState,
                        onExpandPlayer = { viewModel.navigateTo(Screen.AudioPlayer) }
                    )
                }

                // Core 5-tab Navigation Bar
                NoorBottomBar(
                    currentScreen = currentScreen,
                    onTabSelected = { targetScreen -> viewModel.navigateTo(targetScreen) }
                )
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                is Screen.Home -> HomeScreen(viewModel = viewModel)
                is Screen.Mushaf -> MushafScreen(viewModel = viewModel)
                is Screen.Index -> IndexScreen(viewModel = viewModel)
                is Screen.Search -> QuranSearchScreen(viewModel = viewModel)
                is Screen.AudioPlayer -> AudioPlayerScreen(viewModel = viewModel)
                is Screen.Tafsir -> TafsirScreen(viewModel = viewModel)
                is Screen.Memorization -> MemorizationScreen(viewModel = viewModel)
                is Screen.Athkar -> AthkarScreen(viewModel = viewModel)
                is Screen.Tasbih -> DigitalTasbihScreen(viewModel = viewModel)
                is Screen.PrayerQibla -> PrayerQiblaScreen(viewModel = viewModel)
                is Screen.Settings -> SettingsScreen(viewModel = viewModel)
                is Screen.Downloads -> DownloadsScreen(viewModel = viewModel)
                is Screen.Profile -> ProfileScreen(viewModel = viewModel)
                is Screen.AiTutor -> AiTutorScreen(viewModel = viewModel)
                is Screen.RecitationPractice -> RecitationPracticeScreen(viewModel = viewModel)
                is Screen.Topics -> TopicsAndCollectionsScreen(viewModel = viewModel)
                is Screen.Collections -> TopicsAndCollectionsScreen(viewModel = viewModel)
                is Screen.Khatma -> KhatmaScreen(viewModel = viewModel)
            }

            // Global Ayah Context Menu Bottom Sheet
            contextMenuAyah?.let { (surah, ayah) ->
                AyahContextMenuSheet(
                    surah = surah,
                    ayah = ayah,
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeAyahContextMenu() }
                )
            }
        }
    }
}
