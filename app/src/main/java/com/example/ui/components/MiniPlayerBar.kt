package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.audio.AudioPlayerManager
import com.example.data.audio.AudioPlayerState
import com.example.ui.theme.CairoFontFamily
import com.example.ui.theme.EmeraldDeep
import com.example.ui.theme.GoldWarm
import com.example.ui.theme.PureWhite

@Composable
fun MiniPlayerBar(
    audioState: AudioPlayerState,
    onExpandPlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isVisible = audioState.isPlaying || audioState.isBuffering || audioState.currentPositionMs > 0

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            color = EmeraldDeep,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExpandPlayer() }
                .testTag("mini_player_bar")
        ) {
            Column {
                // Linear Progress Indicator
                val progress = if (audioState.durationMs > 0) {
                    (audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()).coerceIn(0f, 1f)
                } else 0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = GoldWarm,
                    trackColor = PureWhite.copy(alpha = 0.2f)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Playback icon badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(GoldWarm.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Audiotrack,
                            contentDescription = "مشغل القرآن",
                            tint = GoldWarm,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title and Reciter
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "سورة ${audioState.currentSurah.nameArabic} - الآية ${audioState.currentAyahNumber}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = audioState.currentReciter.nameArabic,
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = PureWhite.copy(alpha = 0.8f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Previous Ayah
                    IconButton(
                        onClick = { AudioPlayerManager.playPrevious() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("player_prev_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "السابق",
                            tint = PureWhite
                        )
                    }

                    // Play/Pause button
                    IconButton(
                        onClick = { AudioPlayerManager.togglePlayPause() },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoldWarm)
                            .testTag("player_play_pause_button")
                    ) {
                        if (audioState.isBuffering) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = EmeraldDeep,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (audioState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (audioState.isPlaying) "إيقاف مؤقت" else "تشغيل",
                                tint = EmeraldDeep
                            )
                        }
                    }

                    // Next Ayah
                    IconButton(
                        onClick = { AudioPlayerManager.playNext() },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("player_next_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "التالي",
                            tint = PureWhite
                        )
                    }
                }
            }
        }
    }
}
