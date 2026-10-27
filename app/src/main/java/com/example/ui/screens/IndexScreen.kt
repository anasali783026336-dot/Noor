package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.BookmarkEntity
import com.example.data.model.JuzInfo
import com.example.data.model.Surah
import com.example.data.repository.QuranData
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun IndexScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var filterQuery by remember { mutableStateOf("") }
    val bookmarks by viewModel.bookmarks.collectAsState()

    val tabs = listOf("السور (١١٤)", "الأجزاء (٣٠)", "العلامات المرجعية")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("index_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldDeep,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = GoldBright
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontFamily = CairoFontFamily,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Filter / Search Bar for Surahs & Ajza
        if (selectedTabIndex == 0) {
            OutlinedTextField(
                value = filterQuery,
                onValueChange = { filterQuery = it },
                placeholder = {
                    Text(
                        text = "ابحث عن اسم السورة أو رقمها...",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EmeraldDeep)
                },
                trailingIcon = {
                    if (filterQuery.isNotEmpty()) {
                        IconButton(onClick = { filterQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EmeraldDeep,
                    unfocusedBorderColor = BorderGold.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
                    .testTag("surah_filter_field")
            )
        }

        // Tab Content
        when (selectedTabIndex) {
            0 -> {
                // Surahs List
                val filteredSurahs = remember(filterQuery) {
                    if (filterQuery.isBlank()) {
                        QuranData.surahsList
                    } else {
                        val norm = QuranData.normalizeArabic(filterQuery)
                        QuranData.surahsList.filter {
                            QuranData.normalizeArabic(it.nameArabic).contains(norm) ||
                                    it.number.toString().contains(filterQuery)
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        SurahRowItem(
                            surah = surah,
                            onClick = { viewModel.openSurah(surah.number) }
                        )
                    }
                }
            }
            1 -> {
                // Ajza List (30 parts)
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(QuranData.juzList, key = { it.number }) { juz ->
                        JuzRowItem(
                            juz = juz,
                            onClick = { viewModel.openPage(juz.startPage) }
                        )
                    }
                }
            }
            2 -> {
                // Bookmarks List
                if (bookmarks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = GoldMuted,
                                modifier = Modifier.size(56.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد علامات مرجعية حتى الآن",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "انقر على رمز العلامة المرجعية في أي آية لحفظها هنا",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentPadding = PaddingValues(bottom = 100.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(bookmarks, key = { it.id }) { bookmark ->
                            BookmarkRowItem(
                                bookmark = bookmark,
                                onClick = { viewModel.openSurah(bookmark.surahNumber, bookmark.ayahNumber) },
                                onDelete = { viewModel.toggleBookmark(
                                    QuranData.surahsList.first { it.number == bookmark.surahNumber },
                                    com.example.data.model.Ayah(
                                        number = 0,
                                        numberInSurah = bookmark.ayahNumber,
                                        surahNumber = bookmark.surahNumber,
                                        textUthmani = bookmark.ayahText,
                                        textSimple = "",
                                        page = bookmark.pageNumber,
                                        juz = 1
                                    )
                                ) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SurahRowItem(
    surah: Surah,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag("surah_item_${surah.number}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number inside ornamental badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(GoldWarm.copy(alpha = 0.15f))
                    .border(1.dp, GoldMuted, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EmeraldDeep
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Surah Arabic & English Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "سورة ${surah.nameArabic}",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${surah.nameEnglish} • ${surah.revelationType.titleArabic} • ${surah.versesCount} آية",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Page Number Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = EmeraldDeep.copy(alpha = 0.1f)
            ) {
                Text(
                    text = "ص ${surah.startPage}",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDeep,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun JuzRowItem(
    juz: JuzInfo,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(EmeraldDeep),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${juz.number}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = juz.nameArabic,
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "يبدأ من سورة ${juz.startSurahName} (آية ${juz.startAyahNumber})",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = GoldMuted.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "ص ${juz.startPage}",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldBright,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun BookmarkRowItem(
    bookmark: BookmarkEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldBright.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = GoldBright,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "سورة ${bookmark.surahName} - الآية ${bookmark.ayahNumber}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EmeraldDeep
                )
                Text(
                    text = bookmark.ayahText,
                    fontFamily = AmiriFontFamily,
                    fontSize = 14.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "صفحة ${bookmark.pageNumber}",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = GoldMuted
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "حذف العلامة",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
