package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.local.MemorizationEntity
import com.example.data.repository.QuranData
import com.example.data.repository.QuranVersesRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemorizationScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.memorizationItems.collectAsState()
    val selfTestRevealedMap by viewModel.selfTestRevealedMap.collectAsState()

    var activeTestingSurah by remember { mutableStateOf<MemorizationEntity?>(null) }
    var showAddPlanDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("memorization_screen")
    ) {
        if (activeTestingSurah != null) {
            // Self-Test Mode View
            val testPlan = activeTestingSurah!!
            val verses = remember(testPlan.surahNumber) {
                QuranVersesRepository.getVersesForSurah(testPlan.surahNumber)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "وضع التسميع الذاتي: سورة ${testPlan.surahName}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = EmeraldDeep
                )
                IconButton(onClick = {
                    activeTestingSurah = null
                    viewModel.resetSelfTest()
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "خروج من التسميع")
                }
            }

            Text(
                text = "اقرأ من حفظك، ثم انقر على الآية لكشف النص والتأكد من صحة تلاوتك",
                fontFamily = CairoFontFamily,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(verses, key = { it.numberInSurah }) { ayah ->
                    val isRevealed = selfTestRevealedMap[ayah.numberInSurah] == true

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRevealed) MaterialTheme.colorScheme.surface else GoldWarm.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, if (isRevealed) BorderGold else GoldMuted, RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleVerseReveal(ayah.numberInSurah) }
                            .testTag("self_test_ayah_${ayah.numberInSurah}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الآية ${ayah.numberInSurah}",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EmeraldDeep
                                )
                                Icon(
                                    imageVector = if (isRevealed) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = if (isRevealed) EmeraldMedium else GoldMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (isRevealed) {
                                Text(
                                    text = ayah.textUthmani,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 20.sp,
                                    lineHeight = 36.sp,
                                    textAlign = TextAlign.Right,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = "•••••••••••••••••••••••••••••• (انقر للكشف)",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 14.sp,
                                    textAlign = TextAlign.Center,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    viewModel.toggleMemorizedStatus(testPlan)
                    activeTestingSurah = null
                    viewModel.resetSelfTest()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Text("إتمام التسميع وتحديث سجل الحفظ", fontFamily = CairoFontFamily, color = PureWhite)
            }
        } else {
            // Plans Overview
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "خطط الحفظ والمراجعة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Button(
                    onClick = { showAddPlanDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("خطة جديدة", fontFamily = CairoFontFamily, fontSize = 12.sp, color = PureWhite)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    MemorizationPlanCard(
                        item = item,
                        onStartTest = { activeTestingSurah = item },
                        onToggleMemorized = { viewModel.toggleMemorizedStatus(item) }
                    )
                }
            }
        }
    }

    // Add Plan Dialog
    if (showAddPlanDialog) {
        var selectedSurahNum by remember { mutableStateOf(1) }

        AlertDialog(
            onDismissRequest = { showAddPlanDialog = false },
            title = {
                Text("إضافة سورة لخطة الحفظ", fontFamily = CairoFontFamily, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("اختر السورة لبدء خطة الحفظ:", fontFamily = CairoFontFamily, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(QuranData.surahsList) { surah ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedSurahNum = surah.number }
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${surah.number}. سورة ${surah.nameArabic}",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = if (selectedSurahNum == surah.number) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSurahNum == surah.number) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                                )
                                if (selectedSurahNum == surah.number) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = EmeraldDeep)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val surah = QuranData.surahsList.first { it.number == selectedSurahNum }
                        viewModel.openSurah(surah.number)
                        showAddPlanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("إضافة وبدء الحفظ", fontFamily = CairoFontFamily, color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddPlanDialog = false }) {
                    Text("إلغاء", fontFamily = CairoFontFamily)
                }
            }
        )
    }
}

@Composable
fun MemorizationPlanCard(
    item: MemorizationEntity,
    onStartTest: () -> Unit,
    onToggleMemorized: () -> Unit
) {
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
                Column {
                    Text(
                        text = "سورة ${item.surahName}",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الآيات: ${item.fromAyah} إلى ${item.toAyah} • تمت المراجعة ${item.reviewCount} مرات",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isMemorized) EmeraldLight.copy(alpha = 0.2f) else GoldWarm.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = if (item.isMemorized) "تم الحفظ ✓" else "قيد الحفظ",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isMemorized) EmeraldDeep else GoldBright,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress bar
            LinearProgressIndicator(
                progress = { item.masteryLevel / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (item.isMemorized) EmeraldMedium else GoldBright,
                trackColor = BorderGold.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onToggleMemorized,
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (item.isMemorized) "تحديد كغير متمم" else "تحديد كمحفوظ",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onStartTest,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("start_self_test_${item.surahNumber}")
                ) {
                    Icon(imageVector = Icons.Default.Hearing, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("اختبار التسميع", fontFamily = CairoFontFamily, fontSize = 11.sp, color = PureWhite)
                }
            }
        }
    }
}
