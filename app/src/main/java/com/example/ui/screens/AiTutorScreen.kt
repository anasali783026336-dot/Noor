package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.data.ai.QuizItem
import com.example.data.ai.TutorMessage
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun AiTutorScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.tutorMessages.collectAsState()
    val isThinking by viewModel.isTutorThinking.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickPrompts = listOf(
        "ما سبب نزول سورة الكهف؟",
        "اشرح لي فضل آية الكرسي",
        "اختبرني في جزء عم",
        "ضع لي خطة لحفظ سورة الملك في أسبوع",
        "ما معنى ﴿غَاسِقٍ إِذَا وَقَبَ﴾؟",
        "آيات عن الصبر وبشائر الفرج"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("ai_tutor_screen")
    ) {
        // Anti-Hallucination & Authentic Verification Banner
        Surface(
            color = EmeraldDeep,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = GoldBright,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "مرشد موثوق يسترجع إجاباته من مصاحف مجمع الملك فهد والتفاسير المعتمدة. لا يغني عن التلقي من المشايخ بالسند.",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = PureWhite.copy(alpha = 0.95f),
                    lineHeight = 16.sp
                )
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                TutorMessageBubble(
                    message = msg,
                    onAyahClick = { surah, ayah ->
                        viewModel.openSurah(surah, ayah)
                    },
                    onTafsirClick = { surah, ayah ->
                        viewModel.openTafsirForAyah(surah, ayah)
                    }
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = GoldBright,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "المعلم القرآني يبحث في المصادر المعتمدة والتفاسير...",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldMuted
                        )
                    }
                }
            }
        }

        // Quick Suggestion Prompts Carousel
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(quickPrompts) { prompt ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderGold.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { viewModel.sendTutorMessage(prompt) }
                ) {
                    Text(
                        text = prompt,
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "اسأل عن آية، تفسير، سبب نزول، أو اطلب اختباراً...",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tutor_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EmeraldDeep,
                        unfocusedBorderColor = BorderGold.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Send Button
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val textToSend = inputText
                            inputText = ""
                            viewModel.sendTutorMessage(textToSend)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isThinking,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isThinking) EmeraldDeep else MaterialTheme.colorScheme.surfaceVariant)
                        .testTag("tutor_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "إرسال",
                        tint = if (inputText.isNotBlank() && !isThinking) PureWhite else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun TutorMessageBubble(
    message: TutorMessage,
    onAyahClick: (Int, Int) -> Unit,
    onTafsirClick: (Int, Int) -> Unit
) {
    val isUser = message.isUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Tutor Avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(EmeraldDeep)
                    .border(1.dp, GoldBright, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = GoldWarm,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                color = if (isUser) EmeraldDeep else MaterialTheme.colorScheme.surface,
                border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, BorderGold.copy(alpha = 0.35f)) else null,
                tonalElevation = if (isUser) 0.dp else 1.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = message.text,
                        fontFamily = CairoFontFamily,
                        fontSize = 13.sp,
                        lineHeight = 22.sp,
                        color = if (isUser) PureWhite else MaterialTheme.colorScheme.onSurface
                    )

                    // Referenced Ayah Box
                    if (!isUser && message.referencedAyahText != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldWarm.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldMuted.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "﴿${message.referencedAyahText}﴾",
                                fontFamily = AmiriFontFamily,
                                fontSize = 16.sp,
                                lineHeight = 28.sp,
                                textAlign = TextAlign.Center,
                                color = EmeraldDeep,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    // Clickable Citations
                    if (!isUser && message.citations.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = BorderGold.copy(alpha = 0.25f))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "المراجع الموثقة:",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = EmeraldDeep
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        message.citations.forEach { citation ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        if (citation.surahNumber != null && citation.ayahNumber != null) {
                                            onTafsirClick(citation.surahNumber, citation.ayahNumber)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = GoldBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${citation.title} — ${citation.referenceSource}",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Interactive Quiz Card
                    if (!isUser && message.quizItem != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        TutorQuizView(quiz = message.quizItem)
                    }
                }
            }
        }
    }
}

@Composable
fun TutorQuizView(quiz: QuizItem) {
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var hasAnswered by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = EmeraldDeep.copy(alpha = 0.06f),
        border = androidx.compose.foundation.BorderStroke(1.dp, GoldBright.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Quiz, contentDescription = null, tint = GoldBright)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "اختبار قرآني تدريبي:",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EmeraldDeep
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = quiz.question,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            quiz.options.forEachIndexed { index, option ->
                val isSelected = selectedOptionIndex == index
                val isCorrect = quiz.correctIndex == index

                val optionBg = when {
                    !hasAnswered -> if (isSelected) GoldWarm.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                    isCorrect -> EmeraldLight.copy(alpha = 0.25f)
                    isSelected -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }

                val optionBorder = when {
                    !hasAnswered -> if (isSelected) GoldBright else BorderGold.copy(alpha = 0.3f)
                    isCorrect -> EmeraldMedium
                    isSelected -> Color(0xFFEF4444)
                    else -> BorderGold.copy(alpha = 0.2f)
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = optionBg,
                    border = androidx.compose.foundation.BorderStroke(1.dp, optionBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = !hasAnswered) {
                            selectedOptionIndex = index
                            hasAnswered = true
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('أ'.code + index).toChar()})",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = EmeraldDeep
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = option,
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (hasAnswered) {
                Spacer(modifier = Modifier.height(8.dp))
                val isCorrect = selectedOptionIndex == quiz.correctIndex
                Text(
                    text = if (isCorrect) "✓ إجابة صحيحة بارك الله فيك!" else "✗ إجابة غير دقيقة. الصحيح: ${quiz.options[quiz.correctIndex]}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isCorrect) EmeraldMedium else Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = quiz.explanation,
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
