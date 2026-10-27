package com.example.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CityLocation
import com.example.data.model.MajorIslamicCities
import com.example.ui.theme.*
import com.example.ui.viewmodel.NoorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayerQiblaScreen(
    viewModel: NoorViewModel,
    modifier: Modifier = Modifier
) {
    val prayerSchedule by viewModel.prayerSchedule.collectAsState()
    val userSettings by viewModel.userSettings.collectAsState()
    var showCitySheet by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var deviceAzimuth by remember { mutableFloatStateOf(0f) }

    // Register sensor listener for real compass orientation
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event != null && event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientationAngles = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    deviceAzimuth = (azimuth + 360f) % 360f
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    // Animated compass rotation
    val animatedCompassRotation by animateFloatAsState(targetValue = -deviceAzimuth, label = "compass")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("prayer_qibla_screen"),
        contentPadding = PaddingValues(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // City Selector & Current Date Header
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldDeep),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, GoldBright, RoundedCornerShape(16.dp))
                    .clickable { showCitySheet = true }
                    .testTag("prayer_city_selector_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GoldWarm.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = GoldWarm
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "${userSettings.selectedCity.nameArabic} (${userSettings.selectedCity.country})",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = PureWhite
                            )
                            Text(
                                text = "الصلاة القادمة: ${prayerSchedule.nextPrayerName} (${prayerSchedule.timeToNextPrayer})",
                                fontFamily = CairoFontFamily,
                                fontSize = 11.sp,
                                color = GoldWarm
                            )
                        }
                    }

                    Text(
                        text = "تغيير",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = GoldBright,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Prayer Times List Cards
        item {
            val prayerRows = listOf(
                "الفجر" to prayerSchedule.fajr,
                "الشروق" to prayerSchedule.sunrise,
                "الظهر" to prayerSchedule.dhuhr,
                "العصر" to prayerSchedule.asr,
                "المغرب" to prayerSchedule.maghrib,
                "العشاء" to prayerSchedule.isha
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    prayerRows.forEach { (name, time) ->
                        val isNext = name == prayerSchedule.nextPrayerName
                        val isCurrent = name == prayerSchedule.currentPrayerName

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isNext) EmeraldDeep.copy(alpha = 0.1f) else Color.Transparent
                                )
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isNext) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldDeep)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = name,
                                    fontFamily = CairoFontFamily,
                                    fontWeight = if (isNext) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp,
                                    color = if (isNext) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Text(
                                text = time,
                                fontFamily = CairoFontFamily,
                                fontWeight = if (isNext) FontWeight.Bold else FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = if (isNext) EmeraldDeep else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Interactive Qibla Compass Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGold.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "بوصلة القبلة المشرفة",
                        fontFamily = CairoFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = EmeraldDeep
                    )
                    Text(
                        text = "زاوية الكعبة المشرفة: ${prayerSchedule.qiblaBearingDegrees.toInt()}° بالنسبة للشمال",
                        fontFamily = CairoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Compass Dial with Centered Kaaba Image
                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(CircleShape)
                            .background(NightSurface)
                            .border(3.dp, GoldBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Rotating Dial
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .rotate(animatedCompassRotation)
                        ) {
                            // North marker
                            Text(
                                text = "N",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.Red,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 6.dp)
                            )
                            // South marker
                            Text(
                                text = "S",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PureWhite.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 6.dp)
                            )
                            // East & West
                            Text(
                                text = "E",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PureWhite.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .padding(end = 8.dp)
                            )
                            Text(
                                text = "W",
                                fontFamily = CairoFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = PureWhite.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.dp)
                            )

                            // Qibla Indicator Needle pointing to the exact Kaaba angle
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(prayerSchedule.qiblaBearingDegrees)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Navigation,
                                    contentDescription = "اتجاه القبلة",
                                    tint = GoldBright,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .align(Alignment.TopCenter)
                                        .padding(top = 18.dp)
                                )
                            }
                        }

                        // Centered Kaaba Artwork Badge
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .border(2.dp, GoldWarm, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_kaaba_qibla_1793092340148),
                                contentDescription = "الكعبة المشرفة",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val angleDiff = kotlin.math.abs(
                        (deviceAzimuth - prayerSchedule.qiblaBearingDegrees + 180 + 360) % 360 - 180
                    )
                    val isAligned = angleDiff < 8f

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAligned) EmeraldMedium else GoldWarm.copy(alpha = 0.2f),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isAligned) "أنت باتجاه القبلة الآن بفضل الله ✓" else "قم بتدوير الهاتف باتجاه السهم الذهبي",
                            fontFamily = CairoFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isAligned) PureWhite else EmeraldDeep,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }

    // City Selection Bottom Sheet
    if (showCitySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCitySheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "اختر مدينتك لمواقيت الصلاة الدقيقة",
                    fontFamily = CairoFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = EmeraldDeep,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 380.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(MajorIslamicCities) { city ->
                        val isSelected = city.nameArabic == userSettings.selectedCity.nameArabic
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) EmeraldDeep.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectCity(city)
                                    showCitySheet = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${city.nameArabic} - ${city.country}",
                                    fontFamily = CairoFontFamily,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = EmeraldDeep
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
