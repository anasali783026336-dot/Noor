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
import com.example.data.audio.AudioPlayerManager
import com.example.data.model.FamousReciters
import com.example.data.model.Reciter
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayerScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val audioState by viewModel.audioState.collectAsState()
    var showRecitersSheet by remember { mutableStateOf(false) }
    var showTimerSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkNight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Reciter Selection Header
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NightSurface),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GoldMuted.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .clickable { showRecitersSheet = true }
                .testTag("reciter_selector_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(GoldWarm.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = GoldWarm,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = audioState.currentReciter.nameArabic,
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PureWhite
                        )
                        Text(
                            text = "رواية ${audioState.currentReciter.narration}",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = GoldMuted
                        )
                    }
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "تغيير القارئ",
                    tint = GoldWarm
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Large Decorative Islamic Artwork Disc
        Box(
            modifier = Modifier
                .size(190.dp)
                .clip(CircleShape)
                .background(NightSurface)
                .border(3.dp, GoldBright, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = GoldWarm,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "سُورَةُ ${audioState.currentSurah.nameArabic}",
                    fontFamily = AmiriFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = PureWhite
                )
                Text(
                    text = "الآية الكريمة ${audioState.currentAyahNumber}",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    color = GoldMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Progress Slider
        val duration = audioState.durationMs
        val position = audioState.currentPositionMs
        val sliderValue = if (duration > 0) position.toFloat() / duration.toFloat() else 0f

        Slider(
            value = sliderValue.coerceIn(0f, 1f),
            onValueChange = { fraction ->
                if (duration > 0) {
                    AudioPlayerManager.seekTo((fraction * duration).toInt())
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = GoldBright,
                activeTrackColor = GoldWarm,
                inactiveTrackColor = PureWhite.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("audio_slider")
        )

        // Time labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatDuration(position),
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = PureWhite.copy(alpha = 0.7f)
            )
            Text(
                text = formatDuration(duration),
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = PureWhite.copy(alpha = 0.7f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Controls Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Repeat Mode Button
            IconButton(
                onClick = {
                    val nextMode = when (audioState.repeatMode) {
                        1 -> 3
                        3 -> 5
                        5 -> 1
                        else -> 1
                    }
                    AudioPlayerManager.setRepeatMode(nextMode)
                },
                modifier = Modifier.testTag("repeat_mode_button")
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (audioState.repeatMode > 1) GoldWarm else Color.Transparent,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${audioState.repeatMode}x",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (audioState.repeatMode > 1) EmeraldDeep else PureWhite
                        )
                    }
                }
            }

            // Skip Previous
            IconButton(
                onClick = { AudioPlayerManager.playPrevious() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "الآية السابقة",
                    tint = PureWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Play / Pause Primary Button
            Button(
                onClick = { AudioPlayerManager.togglePlayPause() },
                colors = ButtonDefaults.buttonColors(containerColor = GoldBright),
                shape = CircleShape,
                modifier = Modifier
                    .size(68.dp)
                    .testTag("full_player_play_pause_button"),
                contentPadding = PaddingValues(0.dp)
            ) {
                if (audioState.isBuffering) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = EmeraldDeep,
                        strokeWidth = 3.dp
                    )
                } else {
                    Icon(
                        imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (audioState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                        tint = EmeraldDeep,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            // Skip Next
            IconButton(
                onClick = { AudioPlayerManager.playNext() },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "الآية التالية",
                    tint = PureWhite,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Sleep Timer Button
            IconButton(
                onClick = { showTimerSheet = true },
                modifier = Modifier.testTag("sleep_timer_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Snooze,
                    contentDescription = "مؤقت النوم",
                    tint = if (audioState.sleepTimerMinutes > 0) GoldBright else PureWhite
                )
            }
        }
    }

    // Reciters Bottom Sheet
    if (showRecitersSheet) {
        ModalBottomSheet(
            onDismissRequest = { showRecitersSheet = false },
            containerColor = EmeraldDarkNight
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "اختر القارئ المفضل",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldWarm,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(FamousReciters) { reciter ->
                        val isSelected = reciter.id == audioState.currentReciter.id
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EmeraldDeep else NightSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    AudioPlayerManager.setReciter(reciter)
                                    showRecitersSheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = reciter.nameArabic,
                                        fontFamily = CairoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = PureWhite
                                    )
                                    Text(
                                        text = reciter.narration,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 12.sp,
                                        color = GoldMuted
                                    )
                                }
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = GoldBright
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Sleep Timer Sheet
    if (showTimerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTimerSheet = false },
            containerColor = EmeraldDarkNight
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "مؤقت النوم لإيقاف التلاوة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GoldWarm,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                val options = listOf(0 to "إيقاف المؤقت", 15 to "١٥ دقيقة", 30 to "٣٠ دقيقة", 45 to "٤٥ دقيقة", 60 to "ساعة كاملة")
                options.forEach { (mins, label) ->
                    TextButton(
                        onClick = {
                            AudioPlayerManager.setSleepTimer(mins)
                            showTimerSheet = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = label,
                            fontFamily = CairoFontFamily,
                            fontSize = 14.sp,
                            color = if (audioState.sleepTimerMinutes == mins) GoldBright else PureWhite
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun formatDuration(millis: Int): String {
    val totalSeconds = (millis / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
