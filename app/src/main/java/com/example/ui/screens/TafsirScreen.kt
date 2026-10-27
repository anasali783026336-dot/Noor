package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun TafsirScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val tafsir by viewModel.selectedTafsir.collectAsState()
    val context = LocalContext.current

    if (tafsir == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "اختر آية لعرض تفسيرها وتدبرها",
                fontFamily = CairoFontFamily,
                fontSize = 14.sp
            )
        }
        return
    }

    val item = tafsir!!

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("tafsir_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Ayah Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldDeep
                        ) {
                            Text(
                                text = "سورة ${item.surahName} - الآية ${item.ayahNumber}",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        // Audio button
                        IconButton(
                            onClick = { viewModel.playAyahAudio(item.surahNumber, item.ayahNumber) },
                            modifier = Modifier.testTag("tafsir_play_ayah_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "استماع للآية",
                                tint = EmeraldDeep
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "﴿${item.ayahText}﴾",
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        lineHeight = 38.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Ayah & Tafsir", "${item.ayahText}\n\nالتفسير: ${item.tafsirMuyassar}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ نص الآية وتفسيرها", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "﴿${item.ayahText}﴾\n[سورة ${item.surahName}: ${item.ayahNumber}]\n\nالتفسير الميسر:\n${item.tafsirMuyassar}\n\n— تطبيق نور")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "مشاركة التفسير"))
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "مشاركة", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Section 1: التفسير الميسر
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MenuBook, contentDescription = null, tint = EmeraldDeep)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "التفسير الميسر",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EmeraldDeep
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = item.tafsirMuyassar,
                        fontFamily = CairoFontFamily,
                        fontSize = 14.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Section 2: معاني الكلمات
        if (item.vocabWords.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = GoldMuted)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "معاني المفردات والكلمات",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = GoldBright
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        item.vocabWords.forEach { vocab ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = vocab.word,
                                    fontFamily = AmiriFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = EmeraldDeep
                                )
                                Text(
                                    text = vocab.meaning,
                                    fontFamily = CairoFontFamily,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            HorizontalDivider(color = BorderGold.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
        }

        // Section 3: الفوائد والتدبر
        if (item.reflection.isNotBlank()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GoldWarm.copy(alpha = 0.12f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, GoldMuted, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GoldBright)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "فائدة وتدبر",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = EmeraldDeep
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = item.reflection,
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
