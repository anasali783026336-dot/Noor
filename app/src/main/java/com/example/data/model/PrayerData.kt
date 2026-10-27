package com.example.data.model

import java.util.Calendar
import kotlin.math.*

data class CityLocation(
    val nameArabic: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timeZoneOffsetHours: Double
)

val MajorIslamicCities = listOf(
    CityLocation("مكة المكرمة", "المملكة العربية السعودية", 21.4225, 39.8262, 3.0),
    CityLocation("المدينة المنورة", "المملكة العربية السعودية", 24.4672, 39.6111, 3.0),
    CityLocation("القدس الشريف", "فلسطين", 31.7683, 35.2137, 2.0),
    CityLocation("الرياض", "المملكة العربية السعودية", 24.7136, 46.6753, 3.0),
    CityLocation("القاهرة", "مصر", 30.0444, 31.2357, 2.0),
    CityLocation("دمشق", "سوريا", 33.5138, 36.2765, 3.0),
    CityLocation("بغداد", "العراق", 33.3152, 44.3661, 3.0),
    CityLocation("عَمّان", "الأردن", 31.9454, 35.9284, 3.0),
    CityLocation("دبي", "الإمارات العربية المتحدة", 25.2048, 55.2708, 4.0),
    CityLocation("الدوحة", "قطر", 25.2854, 51.5310, 3.0),
    CityLocation("الكويت", "الكويت", 29.3759, 47.9774, 3.0),
    CityLocation("إسطنبول", "تركيا", 41.0082, 28.9784, 3.0),
    CityLocation("الدار البيضاء", "المغرب", 33.5731, -7.5898, 1.0),
    CityLocation("الجزائر", "الجزائر", 36.7538, 3.0588, 1.0),
    CityLocation("تونس", "تونس", 36.8065, 10.1815, 1.0),
    CityLocation("جاكرتا", "إندونيسيا", -6.2088, 106.8456, 7.0),
    CityLocation("كوالالمبور", "ماليزيا", 3.1390, 101.6869, 8.0),
    CityLocation("لندن", "المملكة المتحدة", 51.5074, -0.1278, 0.0)
)

data class PrayerSchedule(
    val cityName: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val currentPrayerName: String,
    val nextPrayerName: String,
    val timeToNextPrayer: String,
    val qiblaBearingDegrees: Float
)

object PrayerCalculator {
    private const val KAABA_LAT = 21.4225
    private const val KAABA_LNG = 39.8262

    // Calculates Qibla bearing from user coordinates towards the Kaaba
    fun calculateQiblaBearing(lat: Double, lng: Double): Float {
        val phiK = Math.toRadians(KAABA_LAT)
        val lambdaK = Math.toRadians(KAABA_LNG)
        val phi = Math.toRadians(lat)
        val lambda = Math.toRadians(lng)

        val numerator = sin(lambdaK - lambda)
        val denominator = cos(phi) * tan(phiK) - sin(phi) * cos(lambdaK - lambda)
        var qibla = Math.toDegrees(atan2(numerator, denominator))
        if (qibla < 0) qibla += 360.0
        return qibla.toFloat()
    }

    // Calculates prayer times for a city on current day
    fun calculatePrayers(city: CityLocation): PrayerSchedule {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val hourNow = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calendar.get(Calendar.MINUTE)
        val currentMinutesOfDay = hourNow * 60 + minuteNow

        // Standard solar declination approximation
        val b = 2 * Math.PI * (dayOfYear - 81) / 365
        val eot = 9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b) // Equation of time in minutes
        val declination = 23.45 * sin(Math.toRadians(360.0 / 365 * (284 + dayOfYear)))

        // Solar noon in minutes
        val timezoneLongitude = city.timeZoneOffsetHours * 15.0
        val timeCorrection = 4 * (city.longitude - timezoneLongitude) + eot
        val solarNoonMinutes = 720 - timeCorrection

        // Angles
        val latRad = Math.toRadians(city.latitude)
        val decRad = Math.toRadians(declination)

        // Fajr (18 degrees below horizon) & Isha (17.5 degrees)
        fun hourAngle(angle: Double): Double {
            val angRad = Math.toRadians(angle)
            val cosHa = (sin(angRad) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))
            val clamped = cosHa.coerceIn(-1.0, 1.0)
            return Math.toDegrees(acos(clamped)) * 4.0
        }

        val haFajr = hourAngle(-18.0)
        val haSunrise = hourAngle(-0.833)
        val haIsha = hourAngle(-17.5)

        // Asr (Shafi/Hanbali/Maliki: shadow = object length + shadow at noon)
        val noonAngle = abs(city.latitude - declination)
        val asrAngle = Math.toDegrees(atan(1.0 + tan(Math.toRadians(noonAngle))))
        val haAsr = hourAngle(90.0 - asrAngle)

        val fajrMin = (solarNoonMinutes - haFajr).toInt()
        val sunriseMin = (solarNoonMinutes - haSunrise).toInt()
        val dhuhrMin = solarNoonMinutes.toInt() + 2 // 2 mins after solar noon
        val asrMin = (solarNoonMinutes + haAsr).toInt()
        val maghribMin = (solarNoonMinutes + haSunrise).toInt() + 2
        val ishaMin = (solarNoonMinutes + haIsha).toInt()

        fun formatTime(min: Int): String {
            val normalized = (min % 1440 + 1440) % 1440
            val h = normalized / 60
            val m = normalized % 60
            val period = if (h < 12) "ص" else "م"
            val displayH = if (h % 12 == 0) 12 else h % 12
            return "%d:%02d %s".format(displayH, m, period)
        }

        // Determine current and next prayer
        val prayers = listOf(
            "الفجر" to fajrMin,
            "الشروق" to sunriseMin,
            "الظهر" to dhuhrMin,
            "العصر" to asrMin,
            "المغرب" to maghribMin,
            "العشاء" to ishaMin
        )

        var currentP = "العشاء"
        var nextP = "الفجر"
        var nextMin = fajrMin + 1440

        for (i in prayers.indices) {
            val p = prayers[i]
            if (currentMinutesOfDay >= p.second) {
                currentP = p.first
                val nextIndex = (i + 1) % prayers.size
                nextP = prayers[nextIndex].first
                nextMin = prayers[nextIndex].second
                if (nextMin <= currentMinutesOfDay) {
                    nextMin += 1440
                }
            }
        }

        val diffMin = nextMin - currentMinutesOfDay
        val remainHours = diffMin / 60
        val remainMins = diffMin % 60
        val remainingStr = if (remainHours > 0) "باقي %d س و %d د".format(remainHours, remainMins) else "باقي %d دقيقة".format(remainMins)

        val qibla = calculateQiblaBearing(city.latitude, city.longitude)

        return PrayerSchedule(
            cityName = city.nameArabic,
            fajr = formatTime(fajrMin),
            sunrise = formatTime(sunriseMin),
            dhuhr = formatTime(dhuhrMin),
            asr = formatTime(asrMin),
            maghrib = formatTime(maghribMin),
            isha = formatTime(ishaMin),
            currentPrayerName = currentP,
            nextPrayerName = nextP,
            timeToNextPrayer = remainingStr,
            qiblaBearingDegrees = qibla
        )
    }
}
