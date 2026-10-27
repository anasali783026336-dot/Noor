package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.audio.AudioPlayerManager
import com.example.data.audio.AudioPlayerState
import com.example.data.local.BookmarkEntity
import com.example.data.local.MemorizationEntity
import com.example.data.local.ReadingProgressEntity
import com.example.data.model.*
import com.example.data.repository.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen(val titleArabic: String) {
    object Home : Screen("الرئيسية")
    object Mushaf : Screen("المصحف الشريف")
    object Index : Screen("الفهرس")
    object Search : Screen("البحث القرآني")
    object AudioPlayer : Screen("مشغل التلاوات")
    object Tafsir : Screen("التفسير والتدبر")
    object Memorization : Screen("الحفظ والمراجعة")
    object Athkar : Screen("الأذكار والأدعية")
    object Tasbih : Screen("السبحة الرقمية")
    object PrayerQibla : Screen("المواقيت والقبلة")
    object Settings : Screen("الإعدادات")
    object Downloads : Screen("المحتوى والتنزيلات")
    object Profile : Screen("الملف الشخصي")
}

class NoorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NoorRepository(application)
    val audioState: StateFlow<AudioPlayerState> = AudioPlayerManager.playerState

    // Navigation Stack
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen = _currentScreen.asStateFlow()

    // Quran Reader State
    private val _selectedSurah = MutableStateFlow(QuranData.surahsList[0])
    val selectedSurah = _selectedSurah.asStateFlow()

    private val _selectedAyahNumber = MutableStateFlow(1)
    val selectedAyahNumber = _selectedAyahNumber.asStateFlow()

    private val _selectedPageNumber = MutableStateFlow(1)
    val selectedPageNumber = _selectedPageNumber.asStateFlow()

    private val _currentVerses = MutableStateFlow<List<Ayah>>(emptyList())
    val currentVerses = _currentVerses.asStateFlow()

    // Tafsir Detail State
    private val _selectedTafsir = MutableStateFlow<TafsirAyah?>(null)
    val selectedTafsir = _selectedTafsir.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Pair<Surah, Ayah>>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // Prayer Schedule State
    private val _prayerSchedule = MutableStateFlow(PrayerCalculator.calculatePrayers(MajorIslamicCities[0]))
    val prayerSchedule = _prayerSchedule.asStateFlow()

    // Tasbih State
    private val _tasbihCount = MutableStateFlow(0)
    val tasbihCount = _tasbihCount.asStateFlow()

    private val _tasbihTarget = MutableStateFlow(33)
    val tasbihTarget = _tasbihTarget.asStateFlow()

    private val _tasbihPhrase = MutableStateFlow("سُبْحَانَ اللَّهِ")
    val tasbihPhrase = _tasbihPhrase.asStateFlow()

    private val _totalTasbihCount = MutableStateFlow(142)
    val totalTasbihCount = _totalTasbihCount.asStateFlow()

    // Memorization Self Test State
    private val _selfTestRevealedMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val selfTestRevealedMap = _selfTestRevealedMap.asStateFlow()

    // Settings & Persistence
    val userSettings = repository.userSettings
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingProgress: StateFlow<ReadingProgressEntity?> = repository.readingProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val memorizationItems: StateFlow<List<MemorizationEntity>> = repository.memorizationItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSurahVerses(1)
        initDefaultMemorizationIfEmpty()
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun openSurah(surahNumber: Int, targetAyahNumber: Int = 1) {
        val surah = QuranData.surahsList.firstOrNull { it.number == surahNumber } ?: QuranData.surahsList[0]
        _selectedSurah.value = surah
        _selectedAyahNumber.value = targetAyahNumber
        _selectedPageNumber.value = surah.startPage
        loadSurahVerses(surahNumber)
        _currentScreen.value = Screen.Mushaf

        viewModelScope.launch {
            repository.updateReadingProgress(surahNumber, targetAyahNumber, surah.startPage)
        }
    }

    fun openPage(pageNumber: Int) {
        val safePage = pageNumber.coerceIn(1, 604)
        _selectedPageNumber.value = safePage
        val surahForPage = QuranData.surahsList.lastOrNull { it.startPage <= safePage } ?: QuranData.surahsList[0]
        _selectedSurah.value = surahForPage
        loadSurahVerses(surahForPage.number)
        _currentScreen.value = Screen.Mushaf

        viewModelScope.launch {
            repository.updateReadingProgress(surahForPage.number, 1, safePage)
        }
    }

    fun loadSurahVerses(surahNumber: Int) {
        val verses = QuranVersesRepository.getVersesForSurah(surahNumber)
        _currentVerses.value = verses
    }

    fun openTafsirForAyah(surahNumber: Int, ayahNumber: Int) {
        val tafsir = QuranVersesRepository.getTafsirForAyah(surahNumber, ayahNumber)
        _selectedTafsir.value = tafsir
        _currentScreen.value = Screen.Tafsir
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.trim().length >= 2) {
            _searchResults.value = QuranVersesRepository.searchAyahs(query)
        } else {
            _searchResults.value = emptyList()
        }
    }

    fun toggleBookmark(surah: Surah, ayah: Ayah) {
        viewModelScope.launch {
            val isBookmarked = bookmarks.value.any { it.surahNumber == surah.number && it.ayahNumber == ayah.numberInSurah }
            if (isBookmarked) {
                repository.removeBookmark(surah.number, ayah.numberInSurah)
            } else {
                repository.addBookmark(
                    surahNumber = surah.number,
                    surahName = surah.nameArabic,
                    ayahNumber = ayah.numberInSurah,
                    ayahText = ayah.textUthmani,
                    pageNumber = ayah.page
                )
            }
        }
    }

    fun playAyahAudio(surahNumber: Int, ayahNumber: Int) {
        AudioPlayerManager.playAyah(surahNumber, ayahNumber)
    }

    // Tasbih interactions
    fun incrementTasbih() {
        val newCount = _tasbihCount.value + 1
        _totalTasbihCount.value = _totalTasbihCount.value + 1
        vibratePhone()
        if (newCount >= _tasbihTarget.value) {
            _tasbihCount.value = 0
            // Advance phrase automatically or keep repeating
        } else {
            _tasbihCount.value = newCount
        }
    }

    fun resetTasbih() {
        _tasbihCount.value = 0
    }

    fun setTasbihPhrase(phrase: String, target: Int = 33) {
        _tasbihPhrase.value = phrase
        _tasbihTarget.value = target
        _tasbihCount.value = 0
    }

    private fun vibratePhone() {
        try {
            val context = getApplication<Application>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(
                    VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK)
                )
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(25, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        } catch (_: Exception) {
            // Ignore vibration failure on emulators without vibrator
        }
    }

    // City & Prayer updates
    fun selectCity(city: CityLocation) {
        repository.setSelectedCity(city)
        _prayerSchedule.value = PrayerCalculator.calculatePrayers(city)
    }

    // Memorization Self-Test
    fun toggleVerseReveal(ayahNumber: Int) {
        val current = _selfTestRevealedMap.value.toMutableMap()
        current[ayahNumber] = !(current[ayahNumber] ?: false)
        _selfTestRevealedMap.value = current
    }

    fun resetSelfTest() {
        _selfTestRevealedMap.value = emptyMap()
    }

    fun toggleMemorizedStatus(item: MemorizationEntity) {
        viewModelScope.launch {
            val updated = item.copy(
                isMemorized = !item.isMemorized,
                masteryLevel = if (!item.isMemorized) 100 else 40,
                reviewCount = item.reviewCount + 1,
                lastReviewTimestamp = System.currentTimeMillis()
            )
            repository.updateMemorizationProgress(updated)
        }
    }

    private fun initDefaultMemorizationIfEmpty() {
        viewModelScope.launch {
            repository.addMemorizationPlan(67, 1, 30) // سورة الملك
            repository.addMemorizationPlan(78, 1, 40) // سورة النبأ
            repository.addMemorizationPlan(112, 1, 4) // سورة الإخلاص
        }
    }

    // Settings forwarders
    fun updateFontSize(newSizeSp: Float) = repository.updateFontSize(newSizeSp)
    fun setReadingMode(mode: MushafReadingMode) = repository.setReadingMode(mode)
    fun setBackgroundTheme(theme: ReadingBackgroundTheme) = repository.setBackgroundTheme(theme)
}
