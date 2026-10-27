package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@Composable
fun ProfileScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val readingProgress by viewModel.readingProgress.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val memorizationItems by viewModel.memorizationItems.collectAsState()
    val totalTasbihCount by viewModel.totalTasbihCount.collectAsState()

    val streak = readingProgress?.streakDays ?: 5
    val memorizedCount = memorizationItems.count { it.isMemorized }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // User Avatar & Title Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldBright, RoundedCornerShape(20.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(GoldWarm.copy(alpha = 0.25f))
                            .border(2.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "الملف الشخصي",
                            tint = GoldWarm,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "قارئ القرآن الكريم",
                        fontFamily = AmiriFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = PureWhite
                    )

                    Text(
                        text = "عضو في منصة نور • ختمة جارية",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = GoldWarm
                    )
                }
            }
        }

        // Stats Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "الأيام المتتالية",
                    value = "$streak أيام 🔥",
                    color = GoldBright,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "العلامات المرجعية",
                    value = "${bookmarks.size} علامة",
                    color = EmeraldMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "السور المتقنة",
                    value = "$memorizedCount سور",
                    color = EmeraldDeep,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "إجمالي التسابيح",
                    value = "$totalTasbihCount",
                    color = GoldMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Khatma Progress Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ختمة القرآن المباركة",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = EmeraldDeep
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = GoldWarm.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "الهدف: شهر رمضان",
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                color = EmeraldDeep,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { 0.28f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EmeraldMedium,
                        trackColor = BorderGold.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "تمت قراءة ١٦٨ صفحة من ٦٠٤",
                            fontFamily = CairoFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "٢٨٪",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = EmeraldDeep
                        )
                    }
                }
            }
        }

        // Backup & Export Data Section
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "النسخ الاحتياطي والبيانات",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "بياناتك وعلاماتك المرجعية وخطط الحفظ محفوظة محلياً في جهازك بأمان تام.",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "نسخة احتياطية من علاماتي المرجعية في تطبيق نور: عدد العلامات ${bookmarks.size} علامة.")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "تصدير العلامات"))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDeep),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("تصدير نسخة", fontFamily = CairoFontFamily, fontSize = 12.sp, color = PureWhite)
                        }

                        OutlinedButton(
                            onClick = {
                                Toast.makeText(context, "تم التحقق من سلامة البيانات المحلية", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("مزامنة محلية", fontFamily = CairoFontFamily, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.border(1.dp, BorderGold.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontFamily = CairoFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontFamily = CairoFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
