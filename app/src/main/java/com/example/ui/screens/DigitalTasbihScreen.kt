package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun DigitalTasbihScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val count by viewModel.tasbihCount.collectAsState()
    val target by viewModel.tasbihTarget.collectAsState()
    val currentPhrase by viewModel.tasbihPhrase.collectAsState()
    val totalCount by viewModel.totalTasbihCount.collectAsState()

    val phrases = listOf(
        "سُبْحَانَ اللَّهِ",
        "الْحَمْدُ لِلَّهِ",
        "لَا إِلَٰهَ إِلَّا اللَّهُ",
        "اللَّهُ أَكْبَرُ",
        "أَسْتَغْفِرُ اللَّهَ",
        "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
        "اللَّهُمَّ صَلِّ عَلَى مُحَمَّدٍ"
    )

    val targets = listOf(33, 100, 1000)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EmeraldDarkNight)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Phrase Picker Row
        Text(
            text = "اختر صيغة التسبيح",
            fontFamily = CairoFontFamily,
            fontSize = 12.sp,
            color = GoldWarm,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(phrases) { phrase ->
                val isSelected = phrase == currentPhrase
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) GoldWarm else NightSurface
                    ),
                    modifier = Modifier
                        .clickable { viewModel.setTasbihPhrase(phrase, target) }
                ) {
                    Text(
                        text = phrase,
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isSelected) EmeraldDeep else PureWhite,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Target Selectors (33 / 100 / 1000)
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            targets.forEach { t ->
                FilterChip(
                    selected = target == t,
                    onClick = { viewModel.setTasbihPhrase(currentPhrase, t) },
                    label = {
                        Text(
                            text = "$t مرة",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = if (target == t) EmeraldDeep else PureWhite
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoldBright,
                        containerColor = NightSurface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Active Phrase Display
        Text(
            text = currentPhrase,
            fontFamily = AmiriFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center,
            color = GoldWarm,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Massive Interactive Tasbih Bead Button
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(NightSurface)
                .clickable { viewModel.incrementTasbih() }
                .testTag("digital_tasbih_large_button"),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$count",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 58.sp,
                    color = PureWhite
                )
                Text(
                    text = "الهدف: $target",
                    fontFamily = CairoFontFamily,
                    fontSize = 14.sp,
                    color = GoldMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = GoldWarm,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اهتزاز تفاعلي",
                        fontFamily = CairoFontFamily,
                        fontSize = 10.sp,
                        color = GoldWarm
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Total Lifetime Counter and Reset Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "إجمالي التسابيح اليوم",
                    fontFamily = CairoFontFamily,
                    fontSize = 12.sp,
                    color = GoldMuted
                )
                Text(
                    text = "$totalCount تسبيحة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = PureWhite
                )
            }

            IconButton(
                onClick = { viewModel.resetTasbih() },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(NightSurface)
                    .testTag("tasbih_reset_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "تصفير العداد",
                    tint = GoldWarm
                )
            }
        }
    }
}
