package com.example.data.model

enum class RevelationType(val titleArabic: String) {
    MECCAN("مكية"),
    MEDINAN("مدنية")
}

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val revelationType: RevelationType,
    val versesCount: Int,
    val revelationOrder: Int,
    val startPage: Int,
    val juzNumber: Int
)

data class Ayah(
    val number: Int,
    val numberInSurah: Int,
    val surahNumber: Int,
    val textUthmani: String,
    val textSimple: String,
    val page: Int,
    val juz: Int,
    val hizbQuarter: Int = 1,
    val hasSajda: Boolean = false
)

data class JuzInfo(
    val number: Int,
    val nameArabic: String,
    val startSurahNumber: Int,
    val startSurahName: String,
    val startAyahNumber: Int,
    val startPage: Int
)

data class HizbInfo(
    val number: Int,
    val nameArabic: String,
    val surahName: String,
    val startAyahNumber: Int,
    val page: Int
)
