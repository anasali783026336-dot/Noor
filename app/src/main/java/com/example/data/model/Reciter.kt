package com.example.data.model

data class Reciter(
    val id: String,
    val nameArabic: String,
    val narration: String,
    val everyAyahSubdir: String,
    val mp3QuranBaseUrl: String
) {
    fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String {
        val sStr = surahNumber.toString().padStart(3, '0')
        val aStr = ayahNumber.toString().padStart(3, '0')
        return "https://everyayah.com/data/$everyAyahSubdir/$sStr$aStr.mp3"
    }

    fun getSurahAudioUrl(surahNumber: Int): String {
        val sStr = surahNumber.toString().padStart(3, '0')
        return "$mp3QuranBaseUrl$sStr.mp3"
    }
}

val FamousReciters = listOf(
    Reciter(
        id = "alafasy",
        nameArabic = "الشيخ مشاري راشد العفاسي",
        narration = "حفص عن عاصم",
        everyAyahSubdir = "Alafasy_128kbps",
        mp3QuranBaseUrl = "https://server8.mp3quran.net/afs/"
    ),
    Reciter(
        id = "abdulbasit",
        nameArabic = "الشيخ عبد الباسط عبد الصمد",
        narration = "المصحف المرتل",
        everyAyahSubdir = "Abdul_Basit_Murattal_192kbps",
        mp3QuranBaseUrl = "https://server7.mp3quran.net/basit/"
    ),
    Reciter(
        id = "hussary",
        nameArabic = "الشيخ محمود خليل الحصري",
        narration = "المصحف المرتل",
        everyAyahSubdir = "Husary_128kbps",
        mp3QuranBaseUrl = "https://server13.mp3quran.net/husr/"
    ),
    Reciter(
        id = "ghamdi",
        nameArabic = "الشيخ سعد الغامدي",
        narration = "حفص عن عاصم",
        everyAyahSubdir = "Ghamadi_40kbps",
        mp3QuranBaseUrl = "https://server7.mp3quran.net/s_gmd/"
    ),
    Reciter(
        id = "muaiqly",
        nameArabic = "الشيخ ماهر المعيقلي",
        narration = "حفص عن عاصم",
        everyAyahSubdir = "MaherAlMuaiqly128kbps",
        mp3QuranBaseUrl = "https://server12.mp3quran.net/maher/"
    )
)
