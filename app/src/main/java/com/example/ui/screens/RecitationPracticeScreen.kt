package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.repository.QuranData
import com.example.data.repository.QuranVersesRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecitationPracticeScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val recitationState by viewModel.recitationState.collectAsState()
    val selectedSurah by viewModel.selectedSurah.collectAsState()
    val currentVerses by viewModel.currentVerses.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    // Default target ayah if none selected yet
    val targetAyah = recitationState.targetAyah ?: currentVerses.firstOrNull() ?: QuranVersesRepository.getVersesForSurah(1).first()
    val targetSurah = recitationState.targetSurah ?: selectedSurah

    // Pulsing animation for microphone when recording
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("recitation_practice_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Islamic Jurisprudence & Practice Disclaimer Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GoldWarm.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldMuted, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = GoldBright,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "هذه الأداة الذكية وسيلة تدريبية وتحفيزية لمراجعة الحفظ، ولا تُغني بحال عن المشافهة والتلقي من المقرئين المتقنين بالسند المتصل.",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Target Ayah Display Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = EmeraldDeep
                    ) {
                        Text(
                            text = "سورة ${targetSurah.nameArabic} — الآية ${targetAyah.numberInSurah}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "﴿${targetAyah.textUthmani}﴾",
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        lineHeight = 40.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Listen to Sheikh Audio comparison button
                    OutlinedButton(
                        onClick = { viewModel.playAyahAudio(targetSurah.number, targetAyah.numberInSurah) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldDeep),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "الاستماع للتلاوة النموذجية",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Recording & Microphone Action Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (recitationState.isRecording) {
                        val minutes = recitationState.recordedDurationSeconds / 60
                        val seconds = recitationState.recordedDurationSeconds % 60
                        Text(
                            text = String.format("%02d:%02d", minutes, seconds),
                            fontFamily = CairoFontFamily,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "جاري الاستماع لتلاوتك الكريمة...",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldMuted
                        )
                    } else {
                        Text(
                            text = "اضغط على اللاقط وابدأ بقراءة الآية مرتلاً",
                            fontFamily = CairoFontFamily,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Big Round Mic Button
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .then(
                                if (recitationState.isRecording) Modifier.scale(pulseScale) else Modifier
                            )
                            .clip(CircleShape)
                            .background(
                                if (recitationState.isRecording) Color(0xFFEF4444) else EmeraldDeep
                            )
                            .border(3.dp, GoldBright, CircleShape)
                            .clickable {
                                if (!hasAudioPermission) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (recitationState.isRecording) {
                                        viewModel.stopRecitationPractice()
                                    } else {
                                        viewModel.startRecitationPractice(targetSurah, targetAyah)
                                    }
                                }
                            }
                            .testTag("recitation_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (recitationState.isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (recitationState.isRecording) "إيقاف التسميع" else "بدء التسميع",
                            tint = PureWhite,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (recitationState.isRecording) "انقر للإيقاف والتقييم" else "بدء التسميع",
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Word-by-Word Evaluation Output
        if (recitationState.recognizedWords.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.TaskAlt, contentDescription = null, tint = EmeraldMedium)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "نتيجة مطابقة التلاوة:",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = EmeraldDeep
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = EmeraldDeep
                            ) {
                                Text(
                                    text = "${recitationState.accuracyPercentage}% مطابقة",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = PureWhite,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = recitationState.feedbackMessage,
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "المقارنة كلمة بكلمة:",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Flow of words
                        androidx.compose.foundation.layout.FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            recitationState.recognizedWords.forEach { (word, isCorrect) ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isCorrect) EmeraldLight.copy(alpha = 0.2f) else GoldWarm.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isCorrect) EmeraldMedium else GoldBright
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = word,
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = if (isCorrect) EmeraldDeep else GoldWarm
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.PriorityHigh,
                                            contentDescription = null,
                                            tint = if (isCorrect) EmeraldMedium else GoldBright,
                                            modifier = Modifier.size(12.dp)
                                        )
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
