package com.example.data.model

data class VocabWord(
    val word: String,
    val meaning: String
)

data class TafsirAyah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val ayahText: String,
    val tafsirMuyassar: String,
    val vocabWords: List<VocabWord> = emptyList(),
    val reflection: String = ""
)
