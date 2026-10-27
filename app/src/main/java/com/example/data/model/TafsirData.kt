package com.example.data.model

enum class TafsirSource(val titleArabic: String, val author: String) {
    MUYASSAR("التفسير الميسر", "نخبة من العلماء بإشراف مجمع الملك فهد"),
    SAADI("تفسير السعدي", "الشيخ عبد الرحمن السعدي رحمه الله"),
    IBN_KATHIR("تفسير ابن كثير", "الإمام الحافظ ابن كثير رحمه الله")
}

data class VocabWord(
    val word: String,
    val meaning: String
)

data class AsbabNuzul(
    val reason: String,
    val authenticSource: String
)

data class AyahIrab(
    val overallGrammar: String,
    val keyWordsGrammar: List<Pair<String, String>> = emptyList()
)

data class AyahTranslation(
    val languageCode: String,
    val languageName: String,
    val translator: String,
    val translatedText: String
)

data class QuranTopic(
    val id: String,
    val titleArabic: String,
    val iconName: String,
    val description: String,
    val ayahsList: List<Triple<Int, Int, String>> // (surahNumber, ayahNumber, title)
)

data class TafsirAyah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val ayahText: String,
    val tafsirMuyassar: String,
    val tafsirSaadi: String = "",
    val tafsirIbnKathir: String = "",
    val vocabWords: List<VocabWord> = emptyList(),
    val asbabNuzul: AsbabNuzul? = null,
    val irab: AyahIrab? = null,
    val translations: Map<String, AyahTranslation> = emptyMap(),
    val reflection: String = ""
)
