package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel
import com.example.ui.viewmodel.Screen

@Composable
fun HomeScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val readingProgress by viewModel.readingProgress.collectAsState()
    val prayerSchedule by viewModel.prayerSchedule.collectAsState()
    val tasbihCount by viewModel.tasbihCount.collectAsState()
    val tasbihTarget by viewModel.tasbihTarget.collectAsState()
    val tasbihPhrase by viewModel.tasbihPhrase.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("home_screen_content"),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Hero Section with Islamic Arch Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_quran_hero_1793092327823),
                    contentDescription = "بانر المنصة الإسلامية",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark & Gold overlay gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    EmeraldDeep.copy(alpha = 0.75f),
                                    EmeraldDeep
                                )
                            )
                        )
                )

                // Greeting & Next Prayer Banner Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "السَّلَامُ عَلَيْكُمْ وَرَحْمَةُ اللَّهِ",
                                fontFamily = AmiriFontFamily,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = GoldWarm
                            )
                            Text(
                                text = "منصة نور القرآنية العالمية",
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                color = PureWhite.copy(alpha = 0.9f)
                            )
                        }

                        // Next prayer countdown pill
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = NightSurface.copy(alpha = 0.85f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldMuted.copy(alpha = 0.6f)),
                            modifier = Modifier.clickable { viewModel.navigateTo(Screen.PrayerQibla) }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessTime,
                                    contentDescription = null,
                                    tint = GoldWarm,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${prayerSchedule.nextPrayerName}: ${prayerSchedule.timeToNextPrayer}",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 11.sp,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }
        }

        // Continue Reading Card (متابعة القراءة)
        item {
            val lastSurah = readingProgress?.lastSurahName ?: "الفاتحة"
            val lastAyah = readingProgress?.lastAyahNumber ?: 1
            val lastPage = readingProgress?.lastPageNumber ?: 1
            val lastSurahNum = readingProgress?.lastSurahNumber ?: 1

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .clickable { viewModel.openSurah(lastSurahNum, lastAyah) }
                    .testTag("continue_reading_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(EmeraldDeep),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = GoldWarm,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "متابعة القراءة",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "سورة $lastSurah - الآية $lastAyah",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "صفحة $lastPage في مصحف المدينة",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldMuted
                        )
                    }

                    Button(
                        onClick = { viewModel.openSurah(lastSurahNum, lastAyah) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("continue_reading_button")
                    ) {
                        Text(
                            text = "متابعة",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }

        // Daily Goal & Memorization Streak Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Daily Quran Goal Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(Screen.Profile) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "الورد اليومي",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = EmeraldMedium,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "٢ من ٤ صفحات",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = EmeraldDeep,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { 0.5f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EmeraldMedium,
                            trackColor = EmeraldMedium.copy(alpha = 0.2f)
                        )
                    }
                }

                // Memorization Streak Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .clickable { viewModel.navigateTo(Screen.Memorization) }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "حفظ ومراجعة",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.Whatshot,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "٥ أيام متتالية 🔥",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "الهدف: سورة الملك",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Quick Access Section Header
        item {
            PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            Text(
                text = "الوصول السريع",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Quick Action Grid (6 icons)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "المصحف الشريف",
                        subtitle = "قراءة وتدبر",
                        icon = Icons.Default.MenuBook,
                        badgeColor = EmeraldDeep,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Mushaf) }
                    )
                    QuickActionTile(
                        title = "مشغل التلاوات",
                        subtitle = "كبار القراء",
                        icon = Icons.Default.Headphones,
                        badgeColor = GoldMuted,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.AudioPlayer) }
                    )
                    QuickActionTile(
                        title = "التفسير ومعانيها",
                        subtitle = "التفسير الميسر",
                        icon = Icons.Default.AutoStories,
                        badgeColor = EmeraldMedium,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.openTafsirForAyah(1, 1) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "السبحة الرقمية",
                        subtitle = "عداد وتسابيح",
                        icon = Icons.Default.Fingerprint,
                        badgeColor = EmeraldDeep,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Tasbih) }
                    )
                    QuickActionTile(
                        title = "الأذكار اليومية",
                        subtitle = "حصن المسلم",
                        icon = Icons.Default.Favorite,
                        badgeColor = GoldMuted,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Athkar) }
                    )
                    QuickActionTile(
                        title = "البحث القرآني",
                        subtitle = "بحث فوري دقيق",
                        icon = Icons.Default.Search,
                        badgeColor = EmeraldMedium,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Search) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionTile(
                        title = "معلم القرآن AI",
                        subtitle = "مرشد واختبارات",
                        icon = Icons.Default.AutoStories,
                        badgeColor = EmeraldDeep,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.AiTutor) }
                    )
                    QuickActionTile(
                        title = "موضوعات القرآن",
                        subtitle = "فهرس ومجموعات",
                        icon = Icons.Default.Category,
                        badgeColor = GoldMuted,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Topics) }
                    )
                    QuickActionTile(
                        title = "ختمات القرآن",
                        subtitle = "تنظيم ومتابعة",
                        icon = Icons.Default.Flag,
                        badgeColor = EmeraldMedium,
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.navigateTo(Screen.Khatma) }
                    )
                }
            }
        }

        // AI Quran Tutor Feature Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, GoldBright, RoundedCornerShape(16.dp))
                    .clickable { viewModel.navigateTo(Screen.AiTutor) }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(GoldWarm.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = GoldWarm,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "معلم القرآن بالذكاء الاصطناعي",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = GoldWarm
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = GoldBright
                            ) {
                                Text(
                                    text = "RAG موثق",
                                    fontFamily = CairoFontFamily,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDeep,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "إجابات فورية مسترجعة من أمهات التفاسير، اختبارات حفظ، ومساعد خطط",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            color = PureWhite.copy(alpha = 0.9f)
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "فتح المعلم",
                        tint = GoldWarm
                    )
                }
            }
        }

        // Ayah of the Day Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(1.dp, BorderGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "آية اليوم وتدبرها",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDeep
                            )
                        }

                        Text(
                            text = "سورة الشرح: ٥",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = GoldMuted
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "﴿فَإِنَّ مَعَ الْعُسْرِ يُسْرًا ۝٥ إِنَّ مَعَ الْعُسْرِ يُسْرًا ۝٦﴾",
                        fontFamily = AmiriFontFamily,
                        fontSize = 20.sp,
                        lineHeight = 34.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "بشارة ربانية لا تخلف: مع كل ضيق وشدة فرج وتيسير، وإذا استحكمت الأزمات جاءت رحمات الله لتفتح للمؤمن آفاق الأمل والسكينة.",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { viewModel.openTafsirForAyah(94, 5) },
                            colors = ButtonDefaults.textButtonColors(contentColor = EmeraldDeep)
                        ) {
                            Text(
                                text = "عرض التفسير الكامل",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }

        // Quick Digital Tasbih Widget
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "تسبيحة سريعة",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = tasbihPhrase,
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = EmeraldDeep
                        )
                        Text(
                            text = "$tasbihCount / $tasbihTarget تسبيحة",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = GoldMuted
                        )
                    }

                    // Interactive Tap Button
                    Button(
                        onClick = { viewModel.incrementTasbih() },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                        shape = CircleShape,
                        modifier = Modifier
                            .size(56.dp)
                            .testTag("home_quick_tasbih_button"),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "+1",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PureWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
            .border(1.dp, BorderGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = badgeColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = subtitle,
                fontFamily = CairoFontFamily,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
