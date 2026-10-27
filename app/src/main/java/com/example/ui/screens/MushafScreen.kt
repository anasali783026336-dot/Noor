package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MushafReadingMode
import com.example.data.repository.QuranData
import com.example.ui.components.AyahItemView
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MushafScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val selectedSurah by viewModel.selectedSurah.collectAsState()
    val selectedPage by viewModel.selectedPageNumber.collectAsState()
    val currentVerses by viewModel.currentVerses.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val audioState by viewModel.audioState.collectAsState()

    var showFontSizeSlider by remember { mutableStateOf(false) }
    var showPageJumpDialog by remember { mutableStateOf(false) }

    val readingMode = userSettings.readingMode
    val fontSizeSp = userSettings.quranFontSizeSp

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                if (MaterialTheme.colorScheme.background == NightDeepBg) NightDeepBg else IvoryWhite
            )
            .testTag("mushaf_screen")
    ) {
        // Quran Reader Sub-Header Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Surah & Juz Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showPageJumpDialog = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = EmeraldDeep,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "سورة ${selectedSurah.nameArabic} (${selectedSurah.revelationType.titleArabic})",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ص $selectedPage • ج ${selectedSurah.juzNumber}",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldMuted
                    )
                }

                // Controls: Font Size, Mode Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Font zoom toggle
                    IconButton(
                        onClick = { showFontSizeSlider = !showFontSizeSlider },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mushaf_font_size_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = "حجم الخط",
                            tint = if (showFontSizeSlider) GoldBright else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Reading Mode Toggle
                    IconButton(
                        onClick = {
                            val newMode = if (readingMode == MushafReadingMode.MADINAH_PAGES) {
                                MushafReadingMode.INTERACTIVE_VERSES
                            } else {
                                MushafReadingMode.MADINAH_PAGES
                            }
                            viewModel.setReadingMode(newMode)
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("mushaf_mode_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (readingMode == MushafReadingMode.MADINAH_PAGES) Icons.Default.ViewAgenda else Icons.Default.AutoStories,
                            contentDescription = "تبديل نمط القراءة",
                            tint = EmeraldDeep
                        )
                    }
                }
            }
        }

        // Animated Font Size Adjuster Panel
        AnimatedVisibility(visible = showFontSizeSlider) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "أ",
                        fontFamily = AmiriFontFamily,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Slider(
                        value = fontSizeSp,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 18f..38f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .testTag("font_size_slider")
                    )
                    Text(
                        text = "أ",
                        fontFamily = AmiriFontFamily,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Reader View based on selected mode
        if (readingMode == MushafReadingMode.MADINAH_PAGES) {
            // Mode A: Madinah Quran Page View
            MadinahPageView(
                pageNumber = selectedPage,
                surah = selectedSurah,
                verses = currentVerses,
                onPreviousPage = {
                    if (selectedPage > 1) viewModel.openPage(selectedPage - 1)
                },
                onNextPage = {
                    if (selectedPage < 604) viewModel.openPage(selectedPage + 1)
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            // Mode B: Interactive Verses View
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .testTag("mushaf_interactive_list"),
                contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp)
            ) {
                // Surah Decorative Header
                item {
                    SurahBannerHeader(surah = selectedSurah)
                }

                // Verses List
                items(currentVerses, key = { it.numberInSurah }) { ayah ->
                    val isBookmarked = bookmarks.any {
                        it.surahNumber == selectedSurah.number && it.ayahNumber == ayah.numberInSurah
                    }
                    val isPlayingThisAyah = audioState.isPlaying &&
                            audioState.currentSurah.number == selectedSurah.number &&
                            audioState.currentAyahNumber == ayah.numberInSurah

                    AyahItemView(
                        surah = selectedSurah,
                        ayah = ayah,
                        isBookmarked = isBookmarked,
                        isPlaying = isPlayingThisAyah,
                        fontSizeSp = fontSizeSp,
                        onPlayClick = { viewModel.playAyahAudio(selectedSurah.number, ayah.numberInSurah) },
                        onTafsirClick = { viewModel.openTafsirForAyah(selectedSurah.number, ayah.numberInSurah) },
                        onBookmarkToggle = { viewModel.toggleBookmark(selectedSurah, ayah) }
                    )
                }
            }
        }
    }

    // Page Jump Dialog
    if (showPageJumpDialog) {
        PageJumpDialog(
            currentPage = selectedPage,
            onDismiss = { showPageJumpDialog = false },
            onPageSelected = { page ->
                viewModel.openPage(page)
                showPageJumpDialog = false
            }
        )
    }
}

@Composable
fun SurahBannerHeader(surah: com.example.data.model.Surah) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .border(1.dp, GoldBright, RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "سُورَةُ ${surah.nameArabic}",
                fontFamily = AmiriFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = GoldWarm
            )
            Text(
                text = "${surah.revelationType.titleArabic} • آياتها ${surah.versesCount} • ترتيب النزول ${surah.revelationOrder}",
                fontFamily = CairoFontFamily,
                fontSize = 12.sp,
                color = PureWhite.copy(alpha = 0.9f)
            )

            if (surah.number != 9) { // No Basmalah for Surah At-Tawbah
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontFamily = AmiriFontFamily,
                    fontSize = 20.sp,
                    color = GoldBright
                )
            }
        }
    }
}

@Composable
fun MadinahPageView(
    pageNumber: Int,
    surah: com.example.data.model.Surah,
    verses: List<com.example.data.model.Ayah>,
    onPreviousPage: () -> Unit,
    onNextPage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Page Container with traditional Quran frame border
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CreamLight)
                .border(2.dp, GoldMuted, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Page Header: Surah and Juz Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "الجزء ${surah.juzNumber}",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                    Text(
                        text = "سورة ${surah.nameArabic}",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GoldMuted.copy(alpha = 0.5f)
                )

                // Page Verses flow in authentic Madinah Mushaf style
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    item {
                        if (pageNumber == surah.startPage && surah.number != 9) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                fontFamily = AmiriFontFamily,
                                fontSize = 22.sp,
                                textAlign = TextAlign.Center,
                                color = EmeraldDeep,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                        }

                        val combinedPageText = verses.joinToString(separator = " ") { it.textUthmani }
                        Text(
                            text = combinedPageText,
                            fontFamily = AmiriFontFamily,
                            fontSize = 22.sp,
                            lineHeight = 44.sp,
                            textAlign = TextAlign.Justify,
                            color = MutedTextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = GoldMuted.copy(alpha = 0.5f)
                )

                // Page Number Indicator
                Text(
                    text = "— $pageNumber —",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = EmeraldDeep,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Bottom Page Flipping Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 80.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onNextPage,
                enabled = pageNumber < 604,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("page_next_button")
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = PureWhite)
                Spacer(modifier = Modifier.width(6.dp))
                Text("الصفحة التالية", fontFamily = CairoFontFamily, fontSize = 12.sp, color = PureWhite)
            }

            Text(
                text = "$pageNumber / 604",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = onPreviousPage,
                enabled = pageNumber > 1,
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("page_prev_button")
            ) {
                Text("الصفحة السابقة", fontFamily = CairoFontFamily, fontSize = 12.sp, color = PureWhite)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = PureWhite)
            }
        }
    }
}

@Composable
fun PageJumpDialog(
    currentPage: Int,
    onDismiss: () -> Unit,
    onPageSelected: (Int) -> Unit
) {
    var targetPage by remember { mutableStateOf(currentPage.toFloat()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "الانتقال السريع إلى صفحة",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "الصفحة: ${targetPage.toInt()}",
                    fontFamily = CairoFontFamily,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep
                )
                Spacer(modifier = Modifier.height(12.dp))
                Slider(
                    value = targetPage,
                    onValueChange = { targetPage = it },
                    valueRange = 1f..604f,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onPageSelected(targetPage.toInt()) },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
            ) {
                Text("انتقال", fontFamily = CairoFontFamily, color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", fontFamily = CairoFontFamily)
            }
        }
    )
}
