package com.example.data.repository

import android.content.Context
import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class MushafReadingMode {
    MADINAH_PAGES,     // عرض صفحات مصحف المدينة
    INTERACTIVE_VERSES // عرض تفاعلي مرن للآيات
}

enum class ReadingBackgroundTheme {
    EMERALD, // أخضر زمردي فاخر
    CREAM,   // عاجي دافئ
    NIGHT,   // ليلي مريح
    WHITE    // أبيض ناصع
}

data class UserSettings(
    val quranFontSizeSp: Float = 24f,
    val readingMode: MushafReadingMode = MushafReadingMode.INTERACTIVE_VERSES,
    val backgroundTheme: ReadingBackgroundTheme = ReadingBackgroundTheme.EMERALD,
    val showTashkeel: Boolean = true,
    val autoPlayNextAyah: Boolean = true,
    val appLanguageCode: String = "ar", // ar, en, fr, ur, id, tr, de, es
    val selectedTafsirSource: TafsirSource = TafsirSource.MUYASSAR,
    val selectedCity: CityLocation = MajorIslamicCities[0] // مكة المكرمة default
)

class NoorRepository(context: Context) {
    private val database = AppDatabase.getDatabase(context)
    private val dao = database.noorDao()

    private val _userSettings = MutableStateFlow(UserSettings())
    val userSettings = _userSettings.asStateFlow()

    // Bookmarks
    val allBookmarks: Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    suspend fun addBookmark(
        surahNumber: Int,
        surahName: String,
        ayahNumber: Int,
        ayahText: String,
        pageNumber: Int,
        note: String = ""
    ) {
        dao.insertBookmark(
            BookmarkEntity(
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayahNumber,
                ayahText = ayahText,
                pageNumber = pageNumber,
                note = note
            )
        )
    }

    suspend fun removeBookmark(surahNumber: Int, ayahNumber: Int) {
        dao.deleteBookmarkByAyah(surahNumber, ayahNumber)
    }

    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean> {
        return dao.isAyahBookmarked(surahNumber, ayahNumber)
    }

    // Reading Progress & Khatma tracker
    val readingProgress: Flow<ReadingProgressEntity?> = dao.getReadingProgress()

    suspend fun updateReadingProgress(surahNumber: Int, ayahNumber: Int, pageNumber: Int) {
        val surah = QuranData.surahsList.firstOrNull { it.number == surahNumber }
        val sName = surah?.nameArabic ?: "الفاتحة"

        dao.saveReadingProgress(
            ReadingProgressEntity(
                id = 1,
                lastSurahNumber = surahNumber,
                lastSurahName = sName,
                lastAyahNumber = ayahNumber,
                lastPageNumber = pageNumber,
                totalVersesRead = 142,
                dailyPagesGoal = 4,
                todayPagesRead = 2,
                streakDays = 5,
                lastReadDateTimestamp = System.currentTimeMillis()
            )
        )
    }

    // Memorization
    val memorizationItems: Flow<List<MemorizationEntity>> = dao.getAllMemorizationItems()

    suspend fun addMemorizationPlan(surahNumber: Int, fromAyah: Int, toAyah: Int) {
        val surah = QuranData.surahsList.firstOrNull { it.number == surahNumber }
        val sName = surah?.nameArabic ?: ""
        dao.insertMemorizationItem(
            MemorizationEntity(
                surahNumber = surahNumber,
                surahName = sName,
                fromAyah = fromAyah,
                toAyah = toAyah,
                isMemorized = false,
                reviewCount = 0,
                masteryLevel = 0
            )
        )
    }

    suspend fun updateMemorizationProgress(item: MemorizationEntity) {
        dao.updateMemorizationItem(item)
    }

    suspend fun deleteMemorizationPlan(item: MemorizationEntity) {
        dao.deleteMemorizationItem(item)
    }

    // Personal Notes on Ayahs
    val allNotes: Flow<List<AyahNoteEntity>> = dao.getAllNotes()

    fun getNotesForAyah(surahNumber: Int, ayahNumber: Int): Flow<List<AyahNoteEntity>> {
        return dao.getNotesForAyah(surahNumber, ayahNumber)
    }

    suspend fun addAyahNote(
        surahNumber: Int,
        ayahNumber: Int,
        surahName: String,
        ayahText: String,
        noteText: String
    ) {
        dao.insertAyahNote(
            AyahNoteEntity(
                surahNumber = surahNumber,
                ayahNumber = ayahNumber,
                surahName = surahName,
                ayahText = ayahText,
                noteText = noteText
            )
        )
    }

    suspend fun deleteAyahNote(note: AyahNoteEntity) {
        dao.deleteAyahNote(note)
    }

    // Ayah Collections
    val allCollections: Flow<List<AyahCollectionEntity>> = dao.getAllCollections()

    suspend fun createCollection(title: String, description: String, colorHex: String = "#064E3B"): Long {
        return dao.insertCollection(
            AyahCollectionEntity(
                titleArabic = title,
                description = description,
                colorHex = colorHex
            )
        )
    }

    suspend fun deleteCollection(collection: AyahCollectionEntity) {
        dao.deleteCollection(collection)
    }

    fun getCollectionItems(collectionId: Int): Flow<List<CollectionAyahItemEntity>> {
        return dao.getItemsForCollection(collectionId)
    }

    suspend fun addAyahToCollection(
        collectionId: Int,
        surahNumber: Int,
        surahName: String,
        ayahNumber: Int,
        ayahText: String,
        pageNumber: Int
    ) {
        dao.insertCollectionItem(
            CollectionAyahItemEntity(
                collectionId = collectionId,
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayahNumber,
                ayahText = ayahText,
                pageNumber = pageNumber
            )
        )
    }

    suspend fun deleteCollectionItem(item: CollectionAyahItemEntity) {
        dao.deleteCollectionItem(item)
    }

    // Khatma Plans
    val allKhatmaPlans: Flow<List<KhatmaPlanEntity>> = dao.getAllKhatmaPlans()

    suspend fun createKhatmaPlan(title: String, targetDays: Int, pagesPerDay: Int) {
        dao.insertKhatmaPlan(
            KhatmaPlanEntity(
                title = title,
                targetDays = targetDays,
                currentPage = 1,
                totalPages = 604,
                pagesPerDay = pagesPerDay
            )
        )
    }

    suspend fun updateKhatmaPage(plan: KhatmaPlanEntity, newPage: Int) {
        val clamped = newPage.coerceIn(1, 604)
        dao.updateKhatmaPlan(
            plan.copy(
                currentPage = clamped,
                isCompleted = clamped >= 604
            )
        )
    }

    suspend fun deleteKhatmaPlan(plan: KhatmaPlanEntity) {
        dao.deleteKhatmaPlan(plan)
    }

    // Settings modifiers
    fun updateFontSize(newSizeSp: Float) {
        _userSettings.value = _userSettings.value.copy(quranFontSizeSp = newSizeSp.coerceIn(18f, 38f))
    }

    fun setReadingMode(mode: MushafReadingMode) {
        _userSettings.value = _userSettings.value.copy(readingMode = mode)
    }

    fun setBackgroundTheme(theme: ReadingBackgroundTheme) {
        _userSettings.value = _userSettings.value.copy(backgroundTheme = theme)
    }

    fun setSelectedCity(city: CityLocation) {
        _userSettings.value = _userSettings.value.copy(selectedCity = city)
    }

    fun setAppLanguage(languageCode: String) {
        _userSettings.value = _userSettings.value.copy(appLanguageCode = languageCode)
    }

    fun setSelectedTafsirSource(source: TafsirSource) {
        _userSettings.value = _userSettings.value.copy(selectedTafsirSource = source)
    }
}
