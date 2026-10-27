package com.example.data.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.data.model.FamousReciters
import com.example.data.model.Reciter
import com.example.data.model.Surah
import com.example.data.repository.QuranData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AudioPlayerState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val currentSurah: Surah = QuranData.surahsList[0],
    val currentAyahNumber: Int = 1,
    val currentReciter: Reciter = FamousReciters[0],
    val currentPositionMs: Int = 0,
    val durationMs: Int = 0,
    val repeatMode: Int = 1, // 1x, 3x, 5x, -1 (continuous)
    val remainingRepeats: Int = 1,
    val sleepTimerMinutes: Int = 0,
    val error: String? = null
)

object AudioPlayerManager {
    private const val TAG = "AudioPlayerManager"
    private var mediaPlayer: MediaPlayer? = null
    private val handler = Handler(Looper.getMainLooper())
    private var progressRunnable: Runnable? = null
    private var sleepTimerRunnable: Runnable? = null

    private val _playerState = MutableStateFlow(AudioPlayerState())
    val playerState: StateFlow<AudioPlayerState> = _playerState.asStateFlow()

    init {
        setupProgressUpdater()
    }

    private fun setupProgressUpdater() {
        progressRunnable = object : Runnable {
            override fun run() {
                mediaPlayer?.let { mp ->
                    if (mp.isPlaying) {
                        val currentPos = mp.currentPosition
                        val dur = mp.duration
                        _playerState.value = _playerState.value.copy(
                            currentPositionMs = currentPos,
                            durationMs = if (dur > 0) dur else _playerState.value.durationMs,
                            isPlaying = true
                        )
                    }
                }
                handler.postDelayed(this, 500)
            }
        }
        handler.post(progressRunnable!!)
    }

    fun playAyah(surahNumber: Int, ayahNumber: Int, reciter: Reciter = _playerState.value.currentReciter) {
        val surah = QuranData.surahsList.firstOrNull { it.number == surahNumber } ?: QuranData.surahsList[0]
        val audioUrl = reciter.getAyahAudioUrl(surahNumber, ayahNumber)

        _playerState.value = _playerState.value.copy(
            currentSurah = surah,
            currentAyahNumber = ayahNumber,
            currentReciter = reciter,
            isBuffering = true,
            error = null
        )

        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                setDataSource(audioUrl)
                setOnPreparedListener { mp ->
                    _playerState.value = _playerState.value.copy(
                        isBuffering = false,
                        isPlaying = true,
                        durationMs = mp.duration
                    )
                    mp.start()
                }
                setOnCompletionListener {
                    handleAyahCompletion()
                }
                setOnErrorListener { _, what, extra ->
                    Log.e(TAG, "MediaPlayer error: what=$what extra=$extra")
                    _playerState.value = _playerState.value.copy(
                        isPlaying = false,
                        isBuffering = false,
                        error = "تعذر تشغيل الصوت. تحقق من الاتصال بالإنترنت."
                    )
                    true
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error playing audio", e)
            _playerState.value = _playerState.value.copy(
                isPlaying = false,
                isBuffering = false,
                error = e.localizedMessage
            )
        }
    }

    private fun handleAyahCompletion() {
        val state = _playerState.value
        if (state.repeatMode > 1 && state.remainingRepeats > 1) {
            // Repeat current ayah
            _playerState.value = state.copy(remainingRepeats = state.remainingRepeats - 1)
            playAyah(state.currentSurah.number, state.currentAyahNumber, state.currentReciter)
        } else {
            // Advance to next ayah in surah if available
            val nextAyah = state.currentAyahNumber + 1
            if (nextAyah <= state.currentSurah.versesCount) {
                _playerState.value = state.copy(remainingRepeats = state.repeatMode)
                playAyah(state.currentSurah.number, nextAyah, state.currentReciter)
            } else {
                // Advance to next surah
                val nextSurahNum = state.currentSurah.number + 1
                if (nextSurahNum <= 114) {
                    _playerState.value = state.copy(remainingRepeats = state.repeatMode)
                    playAyah(nextSurahNum, 1, state.currentReciter)
                } else {
                    _playerState.value = state.copy(isPlaying = false, currentPositionMs = 0)
                }
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            if (mp.isPlaying) {
                mp.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            } else {
                mp.start()
                _playerState.value = _playerState.value.copy(isPlaying = true)
            }
        } else {
            val state = _playerState.value
            playAyah(state.currentSurah.number, state.currentAyahNumber, state.currentReciter)
        }
    }

    fun playNext() {
        val state = _playerState.value
        val nextAyah = state.currentAyahNumber + 1
        if (nextAyah <= state.currentSurah.versesCount) {
            playAyah(state.currentSurah.number, nextAyah, state.currentReciter)
        } else {
            val nextSurahNum = state.currentSurah.number + 1
            if (nextSurahNum <= 114) {
                playAyah(nextSurahNum, 1, state.currentReciter)
            }
        }
    }

    fun playPrevious() {
        val state = _playerState.value
        val prevAyah = state.currentAyahNumber - 1
        if (prevAyah >= 1) {
            playAyah(state.currentSurah.number, prevAyah, state.currentReciter)
        } else {
            val prevSurahNum = state.currentSurah.number - 1
            if (prevSurahNum >= 1) {
                val prevSurah = QuranData.surahsList.firstOrNull { it.number == prevSurahNum }
                val lastAyah = prevSurah?.versesCount ?: 1
                playAyah(prevSurahNum, lastAyah, state.currentReciter)
            }
        }
    }

    fun seekTo(positionMs: Int) {
        mediaPlayer?.seekTo(positionMs)
        _playerState.value = _playerState.value.copy(currentPositionMs = positionMs)
    }

    fun setReciter(reciter: Reciter) {
        val wasPlaying = _playerState.value.isPlaying
        _playerState.value = _playerState.value.copy(currentReciter = reciter)
        if (wasPlaying) {
            val state = _playerState.value
            playAyah(state.currentSurah.number, state.currentAyahNumber, reciter)
        }
    }

    fun setRepeatMode(mode: Int) {
        _playerState.value = _playerState.value.copy(
            repeatMode = mode,
            remainingRepeats = mode
        )
    }

    fun setSleepTimer(minutes: Int) {
        sleepTimerRunnable?.let { handler.removeCallbacks(it) }
        _playerState.value = _playerState.value.copy(sleepTimerMinutes = minutes)

        if (minutes > 0) {
            val task = Runnable {
                pause()
                _playerState.value = _playerState.value.copy(sleepTimerMinutes = 0)
            }
            sleepTimerRunnable = task
            handler.postDelayed(task, minutes * 60 * 1000L)
        }
    }

    fun pause() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                _playerState.value = _playerState.value.copy(isPlaying = false)
            }
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        _playerState.value = _playerState.value.copy(isPlaying = false, currentPositionMs = 0)
    }
}
