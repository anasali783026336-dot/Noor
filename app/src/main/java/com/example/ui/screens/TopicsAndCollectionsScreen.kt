package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahCollectionEntity
import com.example.data.local.AyahNoteEntity
import com.example.data.repository.QuranEncyclopediaData
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

enum class TopicsCollectionsTab(val title: String) {
    TOPICS("موضوعات القرآن"),
    COLLECTIONS("مجموعاتي المختارة"),
    NOTES("خواطري وتدبراتي")
}

@Composable
fun TopicsAndCollectionsScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(TopicsCollectionsTab.TOPICS) }

    val collections by viewModel.ayahCollections.collectAsState()
    val notes by viewModel.ayahNotes.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("topics_collections_screen")
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldDeep,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = GoldBright
                )
            }
        ) {
            TopicsCollectionsTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            fontFamily = CairoFontFamily,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == tab) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            TopicsCollectionsTab.TOPICS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "فهرس الموضوعات القرآنية الكبرى (تصنيف موضوعي موثق):",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    items(QuranEncyclopediaData.quranTopics) { topic ->
                        var isExpanded by remember { mutableStateOf(false) }

                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                .clickable { isExpanded = !isExpanded }
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(EmeraldDeep.copy(alpha = 0.1f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = GoldBright,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = topic.titleArabic,
                                                fontFamily = CairoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = EmeraldDeep
                                            )
                                            Text(
                                                text = topic.description,
                                                fontFamily = CairoFontFamily,
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Icon(
                                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null,
                                        tint = GoldMuted
                                    )
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(modifier = Modifier.padding(top = 14.dp)) {
                                        HorizontalDivider(color = BorderGold.copy(alpha = 0.25f))
                                        Spacer(modifier = Modifier.height(10.dp))
                                        for (triple in topic.ayahsList) {
                                            val surahNum = triple.first
                                            val ayahNum = triple.second
                                            val ayahTitle = triple.third
                                            Surface(
                                                shape = RoundedCornerShape(8.dp),
                                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 3.dp)
                                                    .clickable {
                                                        viewModel.openSurah(surahNum, ayahNum)
                                                    }
                                            ) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Column {
                                                        Text(
                                                            text = ayahTitle,
                                                            fontFamily = CairoFontFamily,
                                                            fontWeight = FontWeight.SemiBold,
                                                            fontSize = 13.sp,
                                                            color = MaterialTheme.colorScheme.onSurface
                                                        )
                                                        Text(
                                                            text = "سورة رقم $surahNum — آية $ayahNum",
                                                            fontFamily = CairoFontFamily,
                                                            fontSize = 10.sp,
                                                            color = GoldMuted
                                                        )
                                                    }

                                                    Row {
                                                        IconButton(
                                                            onClick = { viewModel.playAyahAudio(surahNum, ayahNum) },
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "استماع", tint = EmeraldDeep, modifier = Modifier.size(18.dp))
                                                        }
                                                        IconButton(
                                                            onClick = { viewModel.openSurah(surahNum, ayahNum) },
                                                            modifier = Modifier.size(32.dp)
                                                        ) {
                                                            Icon(imageVector = Icons.Default.OpenInNew, contentDescription = "فتح في المصحف", tint = GoldBright, modifier = Modifier.size(18.dp))
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TopicsCollectionsTab.COLLECTIONS -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "مجموعاتك الخاصة من الآيات الكريمة:",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Button(
                                onClick = { showCreateDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("مجموعة جديدة", fontFamily = CairoFontFamily, fontSize = 11.sp)
                            }
                        }
                    }

                    if (collections.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                            ) {
                                Text(
                                    text = "لا توجد مجموعات بعد. أنشئ مجموعة جديدة وأضف إليها آياتك المفضلة من المصحف.",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    } else {
                        items(collections) { col ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(EmeraldDeep),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.FolderSpecial,
                                                    contentDescription = null,
                                                    tint = GoldWarm,
                                                    modifier = Modifier.size(22.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    text = col.titleArabic,
                                                    fontFamily = CairoFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (col.description.isNotBlank()) {
                                                    Text(
                                                        text = col.description,
                                                        fontFamily = CairoFontFamily,
                                                        fontSize = 11.sp,
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                            }
                                        }

                                        // Play as Audio Playlist Button
                                        IconButton(
                                            onClick = {
                                                // Default playlist item for collection
                                                viewModel.playPlaylist(col.titleArabic, listOf(Pair(1, 1), Pair(2, 255), Pair(67, 1)))
                                                Toast.makeText(context, "جاري تشغيل باقة «${col.titleArabic}»", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayCircle,
                                                contentDescription = "تشغيل كباقة صوتية",
                                                tint = GoldBright,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TopicsCollectionsTab.NOTES -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 14.dp, bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "خواطرك وتدبراتك الشخصية المحفوظة حول الآيات:",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (notes.isEmpty()) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 20.dp)
                            ) {
                                Text(
                                    text = "لم تدون أي خواطر بعد. اضغط على أي آية في المصحف واختر «خاطرة وتدبر» لتدوين تأملاتك.",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                    } else {
                        items(notes) { note ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldDeep
                                        ) {
                                            Text(
                                                text = "سورة ${note.surahName} — آية ${note.ayahNumber}",
                                                fontFamily = CairoFontFamily,
                                                fontSize = 11.sp,
                                                color = PureWhite,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteAyahNote(note) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.DeleteOutline, contentDescription = "حذف الخاطرة", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "﴿${note.ayahText}﴾",
                                        fontFamily = AmiriFontFamily,
                                        fontSize = 15.sp,
                                        color = GoldMuted,
                                        maxLines = 2
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = note.noteText,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        lineHeight = 22.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "إنشاء مجموعة جديدة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("اسم المجموعة", fontFamily = CairoFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newDesc,
                        onValueChange = { newDesc = it },
                        label = { Text("وصف المجموعة", fontFamily = CairoFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.createCollection(newTitle, newDesc)
                            showCreateDialog = false
                            newTitle = ""
                            newDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("حفظ", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}
