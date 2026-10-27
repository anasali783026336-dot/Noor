package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.AiQuranTutorEngine
import com.example.data.model.MajorIslamicCities
import com.example.data.model.PrayerCalculator
import com.example.data.model.RevelationType
import com.example.data.repository.QuranData
import com.example.data.repository.QuranEncyclopediaData
import com.example.data.repository.QuranVersesRepository
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppNameResource() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("نور — NOOR", appName)
    }

    @Test
    fun testQuranSurahsCount() {
        assertEquals(114, QuranData.surahsList.size)
        val fatihah = QuranData.surahsList.first()
        assertEquals(1, fatihah.number)
        assertEquals("الفاتحة", fatihah.nameArabic)
        assertEquals(7, fatihah.versesCount)
        assertEquals(RevelationType.MECCAN, fatihah.revelationType)

        val nas = QuranData.surahsList.last()
        assertEquals(114, nas.number)
        assertEquals("الناس", nas.nameArabic)
        assertEquals(6, nas.versesCount)
    }

    @Test
    fun testArabicNormalization() {
        val original = "ٱلرَّحْمَٰنِ الرَّحِيمِ"
        val normalized = QuranData.normalizeArabic(original)
        assertEquals("الرحمن الرحيم", normalized)
    }

    @Test
    fun testQiblaAndPrayerCalculation() {
        val makkah = MajorIslamicCities[0]
        val schedule = PrayerCalculator.calculatePrayers(makkah)
        assertNotNull(schedule.fajr)
        assertNotNull(schedule.dhuhr)
        assertNotNull(schedule.asr)
        assertNotNull(schedule.maghrib)
        assertNotNull(schedule.isha)
        assertTrue(schedule.qiblaBearingDegrees >= 0f && schedule.qiblaBearingDegrees <= 360f)
    }

    @Test
    fun testQuranVersesRepository() {
        val fatihahVerses = QuranVersesRepository.getVersesForSurah(1)
        assertEquals(7, fatihahVerses.size)
        assertTrue(fatihahVerses[0].textUthmani.contains("بِسْمِ اللَّهِ"))

        val searchResults = QuranVersesRepository.searchAyahs("الرحمن")
        assertTrue(searchResults.isNotEmpty())
    }

    @Test
    fun testQuranEncyclopediaMultiTafsirAndSciences() {
        // Test Ayah Al-Kursi (2:255)
        val tafsirKursi = QuranVersesRepository.getTafsirForAyah(2, 255)
        assertNotNull(tafsirKursi)
        assertTrue(tafsirKursi.tafsirMuyassar.isNotBlank())
        assertTrue(tafsirKursi.tafsirSaadi.isNotBlank())
        assertTrue(tafsirKursi.tafsirIbnKathir.isNotBlank())
        assertTrue(tafsirKursi.vocabWords.isNotEmpty())
        assertNotNull(tafsirKursi.irab)
        assertFalse(tafsirKursi.translations.isEmpty())
        assertTrue(tafsirKursi.translations.containsKey("en"))
        assertTrue(tafsirKursi.translations.containsKey("fr"))
    }

    @Test
    fun testAiQuranTutorEngineQueries() {
        // 1. Asbab Al Nuzul query
        val replyAsbab = AiQuranTutorEngine.processQuery("ما سبب نزول سورة الكهف؟")
        assertFalse(replyAsbab.isUser)
        assertTrue(replyAsbab.text.contains("الكهف") || replyAsbab.text.contains("سبب"))
        assertTrue(replyAsbab.citations.isNotEmpty())

        // 2. Educational Quiz query
        val replyQuiz = AiQuranTutorEngine.processQuery("اختبرني في جزء عم")
        assertNotNull(replyQuiz.quizItem)
        assertEquals(3, replyQuiz.quizItem!!.options.size)
        assertTrue(replyQuiz.quizItem!!.explanation.isNotBlank())

        // 3. Memorization planning query
        val replyMemo = AiQuranTutorEngine.processQuery("ضع لي خطة لحفظ سورة الملك")
        assertTrue(replyMemo.text.contains("الملك") || replyMemo.text.contains("خطة") || replyMemo.text.contains("حفظ"))
    }

    @Test
    fun testQuranTopicalIndex() {
        val topics = QuranEncyclopediaData.quranTopics
        assertTrue(topics.size >= 6)
        val tawheed = topics.firstOrNull { it.id == "tawheed" }
        assertNotNull(tawheed)
        assertTrue(tawheed!!.ayahsList.isNotEmpty())
    }
}
