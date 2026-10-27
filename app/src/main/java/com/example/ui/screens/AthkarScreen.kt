package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.AthkarCategory
import com.example.data.model.AthkarDataProvider
import com.example.data.model.DhikrItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun AthkarScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val categories = AthkarCategory.values()
    val selectedCategory = categories[selectedCategoryIndex]

    // Local mutable map for tracking dhikr counter states
    val countsMap = remember { mutableStateMapOf<Int, Int>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("athkar_screen")
    ) {
        // Floating button / header card for Digital Tasbih
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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
                        text = "السبحة الإلكترونية الرقمية",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = PureWhite
                    )
                    Text(
                        text = "تسبيح تفاعلي حر مع خاصية الاهتزاز",
                        fontFamily = CairoFontFamily,
                        fontSize = 11.sp,
                        color = GoldWarm
                    )
                }

                Button(
                    onClick = { viewModel.navigateTo(Screen.Tasbih) },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldBright),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Fingerprint, contentDescription = null, tint = EmeraldDeep, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فتح السبحة", fontFamily = CairoFontFamily, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EmeraldDeep)
                }
            }
        }

        // Category Scrollable Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedCategoryIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                    color = EmeraldDeep
                )
            }
        ) {
            categories.forEachIndexed { index, cat ->
                Tab(
                    selected = selectedCategoryIndex == index,
                    onClick = { selectedCategoryIndex = index },
                    text = {
                        Text(
                            text = cat.titleArabic,
                            fontFamily = CairoFontFamily,
                            fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (selectedCategoryIndex == index) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Athkar Items for selected category
        val categoryItems = remember(selectedCategory) {
            AthkarDataProvider.items.filter { it.category == selectedCategory }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(categoryItems, key = { it.id }) { item ->
                val currentCount = countsMap[item.id] ?: 0
                val isCompleted = currentCount >= item.targetCount

                DhikrCard(
                    item = item,
                    currentCount = currentCount,
                    isCompleted = isCompleted,
                    onCountClick = {
                        if (currentCount < item.targetCount) {
                            countsMap[item.id] = currentCount + 1
                            viewModel.incrementTasbih()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun DhikrCard(
    item: DhikrItem,
    currentCount: Int,
    isCompleted: Boolean,
    onCountClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) EmeraldLight.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (isCompleted) EmeraldMedium else BorderGold.copy(alpha = 0.35f),
                RoundedCornerShape(16.dp)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Arabic Text
            Text(
                text = item.textArabic,
                fontFamily = AmiriFontFamily,
                fontSize = 18.sp,
                lineHeight = 32.sp,
                textAlign = TextAlign.Right,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Fadl & Hadith Reference
            if (item.fadl.isNotBlank()) {
                Text(
                    text = "• ${item.fadl} (${item.source})",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = GoldMuted,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Counter Button Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isCompleted) "تم الانتهاء بنجاح ✓" else "$currentCount / ${item.targetCount} مرات",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isCompleted) EmeraldMedium else MaterialTheme.colorScheme.onSurfaceVariant
                )

                Button(
                    onClick = onCountClick,
                    enabled = !isCompleted,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) EmeraldMedium else EmeraldDeep,
                        disabledContainerColor = EmeraldMedium.copy(alpha = 0.4f)
                    ),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(52.dp)
                        .testTag("dhikr_counter_${item.id}"),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    if (isCompleted) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = PureWhite)
                    } else {
                        Text(
                            text = "${item.targetCount - currentCount}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}
