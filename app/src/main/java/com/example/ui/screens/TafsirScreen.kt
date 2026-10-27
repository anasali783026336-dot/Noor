package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.example.data.model.TafsirSource
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

enum class TafsirScreenTab(val title: String) {
    TAFSIR("التفاسير"),
    VOCAB("معاني المفردات"),
    ASBAB_NUZUL("أسباب النزول"),
    IRAB("إعراب القرآن"),
    TRANSLATIONS("الترجمات"),
    REFLECTION("فوائد وتدبر")
}

@Composable
fun TafsirScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val tafsir by viewModel.selectedTafsir.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    var selectedTab by remember { mutableStateOf(TafsirScreenTab.TAFSIR) }
    var activeTafsirSource by remember { mutableStateOf(userSettings.selectedTafsirSource) }
    var selectedLanguageCode by remember { mutableStateOf("en") }

    if (tafsir == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = null,
                    tint = GoldMuted,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "اختر آية لعرض تفسيرها وإعرابها وعلومها",
                    fontFamily = CairoFontFamily,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val item = tafsir!!

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("tafsir_screen")
    ) {
        // Quranic Verse Header Card
        Card(
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EmeraldDeep
                    ) {
                        Text(
                            text = "سورة ${item.surahName} — الآية ${item.ayahNumber}",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = PureWhite,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row {
                        // Play Ayah
                        IconButton(
                            onClick = { viewModel.playAyahAudio(item.surahNumber, item.ayahNumber) },
                            modifier = Modifier.testTag("tafsir_play_ayah_button")
                        ) {
                            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "استماع للآية", tint = EmeraldDeep)
                        }

                        // Copy Text
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Ayah & Tafsir", "﴿${item.ayahText}﴾\n\n${item.tafsirMuyassar}")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "تم نسخ نص الآية", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "نسخ", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        // Share
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "﴿${item.ayahText}﴾\n[سورة ${item.surahName}: ${item.ayahNumber}]\n— تطبيق نور للقرآن الكريم")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "مشاركة الآية"))
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = "مشاركة", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "﴿${item.ayahText}﴾",
                    fontFamily = AmiriFontFamily,
                    fontSize = 20.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Scrollable Tabs Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldDeep,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                    color = GoldBright
                )
            }
        ) {
            TafsirScreenTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            fontFamily = CairoFontFamily,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                            color = if (selectedTab == tab) EmeraldDeep else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }
        }

        // Content Area based on Tab
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tafsir_tabs_anim"
        ) { currentTab ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (currentTab) {
                    TafsirScreenTab.TAFSIR -> {
                        // Sub-source selector pills (الميسر / السعدي / ابن كثير)
                        item {
                            Text(
                                text = "اختر مصدر التفسير:",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TafsirSource.values().forEach { src ->
                                    val isSelected = activeTafsirSource == src
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldBright) else null,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(20.dp))
                                            .clickable {
                                                activeTafsirSource = src
                                                viewModel.setSelectedTafsirSource(src)
                                            }
                                    ) {
                                        Text(
                                            text = src.titleArabic,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Selected Tafsir Body Card
                        item {
                            val (tafsirContent, author) = when (activeTafsirSource) {
                                TafsirSource.MUYASSAR -> Pair(item.tafsirMuyassar, TafsirSource.MUYASSAR.author)
                                TafsirSource.SAADI -> Pair(
                                    item.tafsirSaadi.ifBlank { item.tafsirMuyassar },
                                    TafsirSource.SAADI.author
                                )
                                TafsirSource.IBN_KATHIR -> Pair(
                                    item.tafsirIbnKathir.ifBlank { item.tafsirMuyassar },
                                    TafsirSource.IBN_KATHIR.author
                                )
                            }

                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = activeTafsirSource.titleArabic,
                                            fontFamily = CairoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = EmeraldDeep
                                        )
                                        Text(
                                            text = author,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 10.sp,
                                            color = GoldMuted
                                        )
                                    }

                                    HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 10.dp),
                                        color = BorderGold.copy(alpha = 0.3f)
                                    )

                                    Text(
                                        text = tafsirContent,
                                        fontFamily = CairoFontFamily,
                                        fontSize = 14.sp,
                                        lineHeight = 26.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    TafsirScreenTab.VOCAB -> {
                        item {
                            Text(
                                text = "شرح معاني مفردات الآية الكريمة وتراكيبها",
                                fontFamily = CairoFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (item.vocabWords.isEmpty()) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "المفردات جلية وواضحة المعنى في سياق الآية الكريمة.",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
                            items(item.vocabWords) { vocab ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = vocab.word,
                                            fontFamily = AmiriFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = EmeraldDeep
                                        )
                                        Text(
                                            text = vocab.meaning,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    TafsirScreenTab.ASBAB_NUZUL -> {
                        val asbab = item.asbabNuzul
                        if (asbab == null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "هذه الآية نزلت ابتداءً لبيان أحكام الشريعة وعقائد الإيمان، ولم يرد فيها سبب نزول خاص مروي بإسناد محدد.",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.HistoryEdu, contentDescription = null, tint = GoldBright)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "سبب نزول الآية الكريمة",
                                                fontFamily = CairoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = EmeraldDeep
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Text(
                                            text = asbab.reason,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 14.sp,
                                            lineHeight = 25.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            color = BorderGold.copy(alpha = 0.25f)
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.End
                                        ) {
                                            Text(
                                                text = "المصدر: ${asbab.authenticSource}",
                                                fontFamily = CairoFontFamily,
                                                fontSize = 11.sp,
                                                color = GoldMuted,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TafsirScreenTab.IRAB -> {
                        val irab = item.irab
                        if (irab == null) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "إعراب الآية متوافق مع القواعد الإعرابية الجلية، وجارٍ استكمال تفصيل الشواهد النحوية.",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        } else {
                            item {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "الإعراب الكلي والموقع الإعرابي:",
                                            fontFamily = CairoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = EmeraldDeep
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = irab.overallGrammar,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 13.sp,
                                            lineHeight = 23.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            if (irab.keyWordsGrammar.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "إعراب الكلمات المفتاحية:",
                                        fontFamily = CairoFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                items(irab.keyWordsGrammar) { (word, analysis) ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .border(1.dp, BorderGold.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Text(
                                                text = word,
                                                fontFamily = AmiriFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 17.sp,
                                                color = GoldBright
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = analysis,
                                                fontFamily = CairoFontFamily,
                                                fontSize = 12.sp,
                                                lineHeight = 20.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    TafsirScreenTab.TRANSLATIONS -> {
                        item {
                            Text(
                                text = "اختر لغة الترجمة المعتمدة:",
                                fontFamily = CairoFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Horizontal scroll of language selector chips
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val languages = listOf(
                                    Pair("en", "English"),
                                    Pair("fr", "Français"),
                                    Pair("ur", "اردو"),
                                    Pair("id", "Indonesia"),
                                    Pair("tr", "Türkçe"),
                                    Pair("de", "Deutsch"),
                                    Pair("es", "Español")
                                )
                                items(languages) { (code, label) ->
                                    val isSelected = selectedLanguageCode == code
                                    Surface(
                                        shape = RoundedCornerShape(18.dp),
                                        color = if (isSelected) EmeraldDeep else MaterialTheme.colorScheme.surfaceVariant,
                                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, GoldBright) else null,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(18.dp))
                                            .clickable { selectedLanguageCode = code }
                                    ) {
                                        Text(
                                            text = label,
                                            fontFamily = CairoFontFamily,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) PureWhite else MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                        )
                                    }
                                }
                            }
                        }

                        item {
                            val trans = item.translations[selectedLanguageCode]
                            if (trans == null) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                                ) {
                                    Text(
                                        text = "الترجمة لهذه اللغة متوفرة في الإصدار الأحدث من الحزم الموثوقة.",
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            } else {
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                                ) {
                                    Column(modifier = Modifier.padding(18.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = trans.languageName,
                                                fontFamily = CairoFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = EmeraldDeep
                                            )
                                            Text(
                                                text = trans.translator,
                                                fontFamily = CairoFontFamily,
                                                fontSize = 11.sp,
                                                color = GoldMuted
                                            )
                                        }

                                        HorizontalDivider(
                                            modifier = Modifier.padding(vertical = 10.dp),
                                            color = BorderGold.copy(alpha = 0.25f)
                                        )

                                        Text(
                                            text = trans.translatedText,
                                            fontSize = 14.sp,
                                            lineHeight = 24.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }

                    TafsirScreenTab.REFLECTION -> {
                        item {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = GoldWarm.copy(alpha = 0.12f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GoldMuted, RoundedCornerShape(14.dp))
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = GoldBright)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "فائدة وتدبر عملي",
                                            fontFamily = CairoFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = EmeraldDeep
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = item.reflection.ifBlank {
                                            "القرآن الكريم كتاب هداية وعمل؛ تدبَّر معاني هذه الآية واجعل لها أثراً في يقينك ودعائك وسلوكك اليومي."
                                        },
                                        fontFamily = CairoFontFamily,
                                        fontSize = 13.sp,
                                        lineHeight = 23.sp,
                                        color = MaterialTheme.colorScheme.onSurface
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
