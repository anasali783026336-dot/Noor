package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Ayah
import com.example.data.model.Surah
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun QuranSearchScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    val popularKeywords = listOf("الله", "الرحمن", "الصبر", "الجنة", "النور", "التقوى", "الوالدين", "موسى")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("quran_search_screen")
    ) {
        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.onSearchQueryChanged(it) },
            placeholder = {
                Text(
                    text = "اكتب كلمة أو آية للبحث في القرآن الكريم...",
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EmeraldDeep)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EmeraldDeep,
                unfocusedBorderColor = BorderGold.copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .testTag("search_input_field")
        )

        // Popular Keyword Suggestions
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(popularKeywords) { keyword ->
                SuggestionChip(
                    onClick = { viewModel.onSearchQueryChanged(keyword) },
                    label = {
                        Text(
                            text = keyword,
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = EmeraldDeep
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = GoldWarm.copy(alpha = 0.15f)
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = GoldMuted.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Search Results List
        if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = GoldMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "لم يتم العثور على نتائج لـ \"$searchQuery\"",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "جرب البحث بكلمة أخرى أو بجذر الكلمة دون تشكيل",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (searchResults.isNotEmpty()) {
            Text(
                text = "تم العثور على ${searchResults.size} آية كريمة",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = EmeraldDeep,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(searchResults) { (surah, ayah) ->
                    SearchResultCard(
                        surah = surah,
                        ayah = ayah,
                        onClick = { viewModel.openSurah(surah.number, ayah.numberInSurah) }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "ابحث بالرسم الإملائي أو العثماني في جميع سور القرآن الكريم",
                    fontFamily = CairoFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SearchResultCard(
    surah: Surah,
    ayah: Ayah,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "سورة ${surah.nameArabic} - الآية ${ayah.numberInSurah}",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = EmeraldDeep
                )
                Text(
                    text = "ص ${ayah.page} • ج ${ayah.juz}",
                    fontFamily = CairoFontFamily,
                    fontSize = 11.sp,
                    color = GoldMuted
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = ayah.textUthmani,
                fontFamily = AmiriFontFamily,
                fontSize = 17.sp,
                lineHeight = 28.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
