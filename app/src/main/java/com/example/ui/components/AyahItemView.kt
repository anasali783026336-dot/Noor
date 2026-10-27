package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.*

@Composable
fun AyahItemView(
    surah: Surah,
    ayah: Ayah,
    isBookmarked: Boolean,
    isPlaying: Boolean,
    fontSizeSp: Float,
    onPlayClick: () -> Unit,
    onTafsirClick: () -> Unit,
    onBookmarkToggle: () -> Unit,
    onContextMenuClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    val bgColor = when {
        isPlaying -> EmeraldLight.copy(alpha = 0.12f)
        isExpanded -> GoldWarm.copy(alpha = 0.08f)
        else -> Color.Transparent
    }

    val borderColor = when {
        isPlaying -> EmeraldMedium
        isBookmarked -> GoldMuted
        else -> BorderGold.copy(alpha = 0.25f)
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded }
            .testTag("ayah_card_${ayah.numberInSurah}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Ayah badge and quick bookmark star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verse number badge
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoldWarm.copy(alpha = 0.2f))
                        .border(1.dp, GoldMuted, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${ayah.numberInSurah}",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldDeep
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isBookmarked) {
                        Icon(
                            imageVector = Icons.Filled.Bookmark,
                            contentDescription = "محفوظ في العلامات",
                            tint = GoldBright,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    if (isPlaying) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldDeep
                        ) {
                            Text(
                                text = "يتلو الآن",
                                fontFamily = CairoFontFamily,
                                fontSize = 10.sp,
                                color = PureWhite,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Uthmanic Text
            Text(
                text = ayah.textUthmani,
                fontFamily = AmiriFontFamily,
                fontSize = fontSizeSp.sp,
                lineHeight = (fontSizeSp * 1.85f).sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ayah_text_${ayah.numberInSurah}")
            )

            // Quick Action Toolbar when tapped
            AnimatedVisibility(
                visible = isExpanded || isPlaying,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = BorderGold.copy(alpha = 0.3f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play Ayah
                        IconButton(
                            onClick = onPlayClick,
                            modifier = Modifier.testTag("ayah_action_play_${ayah.numberInSurah}")
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = "استماع للآية",
                                tint = EmeraldDeep
                            )
                        }

                        // Tafsir
                        IconButton(
                            onClick = onTafsirClick,
                            modifier = Modifier.testTag("ayah_action_tafsir_${ayah.numberInSurah}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoStories,
                                contentDescription = "التفسير الميسر",
                                tint = EmeraldDeep
                            )
                        }

                        // Bookmark
                        IconButton(
                            onClick = onBookmarkToggle,
                            modifier = Modifier.testTag("ayah_action_bookmark_${ayah.numberInSurah}")
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "علامة مرجعية",
                                tint = if (isBookmarked) GoldBright else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Copy Text
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Quran Ayah", "${ayah.textUthmani} [سورة ${surah.nameArabic}: ${ayah.numberInSurah}]")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ الآية الكريمة", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("ayah_action_copy_${ayah.numberInSurah}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "نسخ الآية",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "﴿${ayah.textUthmani}﴾\n[سورة ${surah.nameArabic} - الآية ${ayah.numberInSurah}]\n— تطبيق نور للقرآن الكريم")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "مشاركة الآية الكريمة"))
                            },
                            modifier = Modifier.testTag("ayah_action_share_${ayah.numberInSurah}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Share,
                                contentDescription = "مشاركة",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // More / Context Menu
                        if (onContextMenuClick != null) {
                            IconButton(
                                onClick = onContextMenuClick,
                                modifier = Modifier.testTag("ayah_action_more_${ayah.numberInSurah}")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.MoreHoriz,
                                    contentDescription = "خيارات إضافية للآية",
                                    tint = EmeraldDeep
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
