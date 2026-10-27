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
import com.example.data.local.KhatmaPlanEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun KhatmaScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val khatmaPlans by viewModel.khatmaPlans.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var planTitle by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(30) }
    var showDuaaKhatm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("khatma_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldBright, RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = GoldWarm,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ختمات القرآن الكريم",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = GoldWarm
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "نظم قراءتك اليومية وحدد هدفك لإتمام ختم كتاب الله في المدة التي تختارها",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        color = PureWhite.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldBright),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = EmeraldDeep, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "بدء ختمة جديدة",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldDeep,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Active Plans
        items(khatmaPlans) { plan ->
            val progressPercent = (plan.currentPage.toFloat() / 604f).coerceIn(0f, 1f)
            val isCompleted = plan.currentPage >= 604

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = plan.title,
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = EmeraldDeep
                            )
                            Text(
                                text = "الهدف: ${plan.targetDays} يوماً • ${plan.pagesPerDay} صفحة يومياً (حوالي ${(plan.pagesPerDay / 20.0).let { String.format("%.1f", it) }} جزء)",
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCompleted) GoldBright else EmeraldDeep
                        ) {
                            Text(
                                text = "${(progressPercent * 100).toInt()}%",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (isCompleted) EmeraldDeep else PureWhite,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    LinearProgressIndicator(
                        progress = { progressPercent },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = GoldBright,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "الصفحة ${plan.currentPage} من 604",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "المتبقي: ${604 - plan.currentPage} صفحة",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = GoldMuted
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = BorderGold.copy(alpha = 0.25f))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Open in Mushaf
                        OutlinedButton(
                            onClick = { viewModel.openPage(plan.currentPage.coerceAtLeast(1)) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("فتح في المصحف", fontFamily = CairoFontFamily, fontSize = 11.sp)
                        }

                        // Mark +1 Page
                        Button(
                            onClick = {
                                val next = (plan.currentPage + 1).coerceAtMost(604)
                                viewModel.updateKhatmaProgress(plan, next)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+١ صفحة", fontFamily = CairoFontFamily, fontSize = 11.sp)
                        }

                        // Mark +1 Juz (20 pages)
                        Button(
                            onClick = {
                                val next = (plan.currentPage + 20).coerceAtMost(604)
                                viewModel.updateKhatmaProgress(plan, next)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldMuted),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+١ جزء", fontFamily = CairoFontFamily, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Collapsible Duaa Khatm Al-Quran
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldWarm.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldMuted, RoundedCornerShape(14.dp))
                    .clickable { showDuaaKhatm = !showDuaaKhatm }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = GoldBright)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "دعاء ختم القرآن الكريم المأثور",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDeep
                            )
                        }
                        Icon(
                            imageVector = if (showDuaaKhatm) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = GoldMuted
                        )
                    }

                    AnimatedVisibility(visible = showDuaaKhatm) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            HorizontalDivider(color = BorderGold.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "«اللَّهُمَّ ارْحَمْنِي بِالقُرْآنِ وَاجْعَلْهُ لِي إِمَاماً وَنُوراً وَهُدًى وَرَحْمَةً، اللَّهُمَّ ذَكِّرْنِي مِنْهُ مَا نَسِيتُ وَعَلِّمْنِي مِنْهُ مَا جَهِلْتُ، وَارْزُقْنِي تِلاَوَتَهُ آنَاءَ اللَّيْلِ وَأَطْرَافَ النَّهَارِ، وَاجْعَلْهُ لِي حُجَّةً يَا رَبَّ العَالَمِينَ»",
                                fontFamily = AmiriFontFamily,
                                fontSize = 16.sp,
                                lineHeight = 30.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    // Create New Khatma Dialog
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = {
                Text(
                    text = "إنشاء خطة ختمة جديدة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = planTitle,
                        onValueChange = { planTitle = it },
                        label = { Text("اسم الختمة (مثال: ختمة رجب، ختمة التثبيت)", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text(
                        text = "المدة المستهدفة لإتمام الختمة:",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(15, 30, 60).forEach { days ->
                            val isSel = selectedDays == days
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) EmeraldDeep else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDays = days }
                            ) {
                                Text(
                                    text = "$days يوماً",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) PureWhite else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }

                    val requiredPagesPerDay = (604.0 / selectedDays.toDouble()).toInt().coerceAtLeast(1)
                    Text(
                        text = "المعدل المطلوب: $requiredPagesPerDay صفحة يومياً (حوالي ${(requiredPagesPerDay / 20.0).let { String.format("%.1f", it) }} جزء)",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldBright,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val title = if (planTitle.isNotBlank()) planTitle else "ختمة $selectedDays يوماً"
                        viewModel.createKhatmaPlan(title, selectedDays)
                        Toast.makeText(context, "تم إنشاء خطة الختمة بنجاح", Toast.LENGTH_SHORT).show()
                        showCreateDialog = false
                        planTitle = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep)
                ) {
                    Text("بدء الختمة", fontFamily = CairoFontFamily)
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
