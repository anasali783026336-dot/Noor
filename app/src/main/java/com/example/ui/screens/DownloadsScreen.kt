package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.QuranData
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

data class DownloadableSurahItem(
    val surahNumber: Int,
    val surahName: String,
    val sizeMb: Double,
    var isDownloaded: Boolean = false,
    var isDownloading: Boolean = false
)

@Composable
fun DownloadsScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val downloadableItems = remember {
        mutableStateListOf(
            DownloadableSurahItem(1, "الفاتحة", 1.2, isDownloaded = true),
            DownloadableSurahItem(18, "الكهف", 14.5, isDownloaded = true),
            DownloadableSurahItem(36, "يس", 8.4, isDownloaded = false),
            DownloadableSurahItem(55, "الرحمن", 7.1, isDownloaded = false),
            DownloadableSurahItem(56, "الواقعة", 6.8, isDownloaded = false),
            DownloadableSurahItem(67, "الملك", 4.3, isDownloaded = true),
            DownloadableSurahItem(78, "النبأ", 3.6, isDownloaded = false),
            DownloadableSurahItem(112, "الإخلاص", 0.5, isDownloaded = true)
        )
    }

    val totalDownloadedSize = remember(downloadableItems) {
        downloadableItems.filter { it.isDownloaded }.sumOf { it.sizeMb }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("downloads_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Storage Status Overview Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldBright, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "المساحة المحملة دون اتصال",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = PureWhite
                        )
                        Icon(imageVector = Icons.Default.CloudDone, contentDescription = null, tint = GoldWarm)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "%.1f ميجابايت".format(totalDownloadedSize),
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = GoldWarm
                    )

                    Text(
                        text = "جميع نصوص القرآن الكريم متاحة أوفلاين دون الحاجة للإنترنت",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = PureWhite.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {
                            downloadableItems.forEachIndexed { index, item ->
                                downloadableItems[index] = item.copy(isDownloaded = false)
                            }
                            Toast.makeText(context, "تم مسح الذاكرة المؤقتة", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PureWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GoldWarm)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تفريغ الذاكرة المؤقتة", fontFamily = CairoFontFamily, fontSize = 12.sp)
                    }
                }
            }
        }

        // Section Title
        item {
            Text(
                text = "حزم التلاوات الصوتية الجاهزة للتنزيل",
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = EmeraldDeep
            )
        }

        // List of surahs available for download
        items(downloadableItems, key = { it.surahNumber }) { item ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
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
                            text = "سورة ${item.surahName}",
                            fontFamily = AmiriFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "حجم الملف: ${item.sizeMb} ميجابايت • بصوت مشاري العفاسي",
                            fontFamily = CairoFontFamily,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (item.isDownloaded) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EmeraldLight.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldDeep,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "تم التنزيل",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = EmeraldDeep
                                )
                            }
                        }
                    } else {
                        Button(
                            onClick = {
                                val idx = downloadableItems.indexOf(item)
                                if (idx != -1) {
                                    downloadableItems[idx] = item.copy(isDownloaded = true)
                                    Toast.makeText(context, "تم تنزيل سورة ${item.surahName} بنجاح", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("تنزيل", fontFamily = CairoFontFamily, fontSize = 11.sp, color = PureWhite)
                        }
                    }
                }
            }
        }
    }
}
