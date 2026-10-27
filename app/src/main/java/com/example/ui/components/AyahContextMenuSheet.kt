package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AyahCollectionEntity
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyahContextMenuSheet(
    surah: Surah,
    ayah: Ayah,
    viewModel: NoorViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val bookmarks by viewModel.bookmarks.collectAsState()
    val collections by viewModel.ayahCollections.collectAsState()

    val isBookmarked = bookmarks.any { it.surahNumber == surah.number && it.ayahNumber == ayah.numberInSurah }

    var showAddNoteDialog by remember { mutableStateOf(false) }
    var noteText by remember { mutableStateOf("") }

    var showCollectionPicker by remember { mutableStateOf(false) }
    var showCreateCollectionDialog by remember { mutableStateOf(false) }
    var newCollectionTitle by remember { mutableStateOf("") }
    var newCollectionDesc by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            BottomSheetDefaults.DragHandle(color = GoldMuted)
        },
        modifier = Modifier.testTag("ayah_context_menu_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .navigationBarsPadding()
        ) {
            // Header: Surah & Ayah Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(GoldWarm.copy(alpha = 0.2f))
                            .border(1.dp, GoldMuted, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${ayah.numberInSurah}",
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "سورة ${surah.nameArabic}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "الآية ${ayah.numberInSurah} • صفحة ${ayah.page} • جزء ${surah.juzNumber}",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = GoldMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ayah Text Preview Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = GoldWarm.copy(alpha = 0.08f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "﴿${ayah.textUthmani}﴾",
                    fontFamily = AmiriFontFamily,
                    fontSize = 19.sp,
                    lineHeight = 34.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Play Audio
                AyahActionTile(
                    title = "استماع",
                    icon = Icons.Default.PlayArrow,
                    iconTint = EmeraldDeep,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.playAyahAudio(surah.number, ayah.numberInSurah)
                        onDismiss()
                    }
                )

                // Tafsir & Sciences
                AyahActionTile(
                    title = "التفسير والبيان",
                    icon = Icons.Default.AutoStories,
                    iconTint = EmeraldDeep,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.openTafsirForAyah(surah.number, ayah.numberInSurah)
                        onDismiss()
                    }
                )

                // Recite & Practice
                AyahActionTile(
                    title = "تسميع صوتي",
                    icon = Icons.Default.Mic,
                    iconTint = GoldBright,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.startRecitationPractice(surah, ayah)
                        viewModel.navigateTo(Screen.RecitationPractice)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bookmark
                AyahActionTile(
                    title = if (isBookmarked) "محفوظة" else "حفظ كعلامة",
                    icon = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    iconTint = if (isBookmarked) GoldBright else EmeraldDeep,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.toggleBookmark(surah, ayah)
                        Toast.makeText(context, if (isBookmarked) "تم حذف العلامة" else "تم حفظ العلامة المرجعية", Toast.LENGTH_SHORT).show()
                    }
                )

                // Personal Reflection Note
                AyahActionTile(
                    title = "خاطرة وتدبر",
                    icon = Icons.Default.EditNote,
                    iconTint = EmeraldMedium,
                    modifier = Modifier.weight(1f),
                    onClick = { showAddNoteDialog = true }
                )

                // Add to Collection
                AyahActionTile(
                    title = "إضافة لمجموعة",
                    icon = Icons.Default.FolderSpecial,
                    iconTint = GoldMuted,
                    modifier = Modifier.weight(1f),
                    onClick = { showCollectionPicker = true }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Copy Text
                AyahActionTile(
                    title = "نسخ النص",
                    icon = Icons.Default.ContentCopy,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Quran Ayah", "﴿${ayah.textUthmani}﴾ [سورة ${surah.nameArabic}: ${ayah.numberInSurah}]")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "تم نسخ الآية الكريمة", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    }
                )

                // Share Ayah
                AyahActionTile(
                    title = "مشاركة الآية",
                    icon = Icons.Default.Share,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(
                                Intent.EXTRA_TEXT,
                                "﴿${ayah.textUthmani}﴾\n[سورة ${surah.nameArabic} - الآية ${ayah.numberInSurah}]\n— تطبيق نور للقرآن الكريم"
                            )
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "مشاركة الآية الكريمة"))
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add Reflection Note Dialog
    if (showAddNoteDialog) {
        AlertDialog(
            onDismissRequest = { showAddNoteDialog = false },
            title = {
                Text(
                    text = "تدوين خاطرة وتدبر للآية",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column {
                    Text(
                        text = "﴿${ayah.textUthmani}﴾",
                        fontFamily = AmiriFontFamily,
                        fontSize = 14.sp,
                        color = GoldMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        placeholder = { Text("اكتب تدبرك الشخصي أو ما استوقف قلبك في هذه الآية...", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .testTag("note_input_field"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (noteText.isNotBlank()) {
                            viewModel.addAyahPersonalNote(surah, ayah, noteText)
                            Toast.makeText(context, "تم حفظ الخاطرة في تدبراتي", Toast.LENGTH_SHORT).show()
                            showAddNoteDialog = false
                            noteText = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("حفظ الخاطرة", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddNoteDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Collection Picker Dialog
    if (showCollectionPicker) {
        AlertDialog(
            onDismissRequest = { showCollectionPicker = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اختر مجموعة لحفظ الآية",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    IconButton(onClick = {
                        showCollectionPicker = false
                        showCreateCollectionDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.AddCircle, contentDescription = "مجموعة جديدة", tint = EmeraldDeep)
                    }
                }
            },
            text = {
                if (collections.isEmpty()) {
                    Text(
                        text = "لا توجد مجموعات حالياً. اضغط علامة + لإنشاء مجموعة جديدة (مثل: آيات السكينة، أدعيتي المستجابة).",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp
                    )
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(collections) { col ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        viewModel.addAyahToCollection(col.id, surah, ayah)
                                        Toast.makeText(context, "أضيفت إلى «${col.titleArabic}»", Toast.LENGTH_SHORT).show()
                                        showCollectionPicker = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = EmeraldDeep)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = col.titleArabic,
                                            fontFamily = CairoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
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
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCollectionPicker = false }) {
                    Text("إغلاق", fontFamily = CairoFontFamily)
                }
            }
        )
    }

    // Create New Collection Dialog
    if (showCreateCollectionDialog) {
        AlertDialog(
            onDismissRequest = { showCreateCollectionDialog = false },
            title = {
                Text(
                    text = "إنشاء مجموعة آيات جديدة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newCollectionTitle,
                        onValueChange = { newCollectionTitle = it },
                        label = { Text("عنوان المجموعة (مثال: آيات الشفاء)", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newCollectionDesc,
                        onValueChange = { newCollectionDesc = it },
                        label = { Text("وصف مختصر للمجموعة", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCollectionTitle.isNotBlank()) {
                            viewModel.createCollection(newCollectionTitle, newCollectionDesc)
                            Toast.makeText(context, "تم إنشاء المجموعة بنجاح", Toast.LENGTH_SHORT).show()
                            showCreateCollectionDialog = false
                            newCollectionTitle = ""
                            newCollectionDesc = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إنشاء", fontFamily = CairoFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateCollectionDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

@Composable
fun AyahActionTile(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag("action_tile_$title")
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
