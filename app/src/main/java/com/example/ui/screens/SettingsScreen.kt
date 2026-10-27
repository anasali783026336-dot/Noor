package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MushafReadingMode
import com.example.data.repository.ReadingBackgroundTheme
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun SettingsScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val userSettings by viewModel.userSettings.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quran Reading Settings Section
        item {
            Text(
                text = "تخصيص القراءة والمصحف",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EmeraldDeep
            )
        }

        // Font Size Setting Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "حجم خط الآيات الكريمة: ${userSettings.quranFontSizeSp.toInt()} نقطة",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "﴿بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ﴾",
                        fontFamily = AmiriFontFamily,
                        fontSize = userSettings.quranFontSizeSp.sp,
                        color = EmeraldDeep
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = userSettings.quranFontSizeSp,
                        onValueChange = { viewModel.updateFontSize(it) },
                        valueRange = 18f..38f
                    )
                }
            }
        }

        // Reading Mode Setting Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "نمط العرض الافتراضي للمصحف",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = userSettings.readingMode == MushafReadingMode.INTERACTIVE_VERSES,
                            onClick = { viewModel.setReadingMode(MushafReadingMode.INTERACTIVE_VERSES) },
                            label = { Text("قراءة تفاعلية مرنة", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldDeep, selectedLabelColor = PureWhite)
                        )
                        FilterChip(
                            selected = userSettings.readingMode == MushafReadingMode.MADINAH_PAGES,
                            onClick = { viewModel.setReadingMode(MushafReadingMode.MADINAH_PAGES) },
                            label = { Text("صفحات مصحف المدينة", fontFamily = CairoFontFamily, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = EmeraldDeep, selectedLabelColor = PureWhite)
                        )
                    }
                }
            }
        }

        // Offline Content & Downloads link
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(Screen.Downloads) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = EmeraldDeep)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "إدارة التنزيلات دون اتصال",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "تنزيل سور كاملة وتلاوات للاستماع دون إنترنت",
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = EmeraldDeep)
                }
            }
        }

        // Profile & Stats link
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(Screen.Profile) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = GoldMuted)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "الملف الشخصي وإحصائيات القراءة",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "سجل الإنجاز والختمات وتصدير النسخة الاحتياطية",
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null, tint = GoldMuted)
                }
            }
        }

        // About NOOR platform card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = GoldWarm.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldMuted, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "عن منصة نور — NOOR",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = EmeraldDeep
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "منصة قرآنية رقمية عالمية مجانية بالكامل لوجه الله تعالى، خالية من أي إعلانات أو اشتراكات، تهدف لتقديم أفضل تجربة لتدبر كتاب الله وحفظه والاستماع لكبار قرائه.",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الإصدار 1.0 • وقف إسلامي رقمي",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldMuted
                    )
                }
            }
        }
    }
}
