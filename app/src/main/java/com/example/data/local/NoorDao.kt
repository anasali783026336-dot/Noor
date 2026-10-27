package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoorDao {
    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Delete
    suspend fun deleteBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber")
    suspend fun deleteBookmarkByAyah(surahNumber: Int, ayahNumber: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber)")
    fun isAyahBookmarked(surahNumber: Int, ayahNumber: Int): Flow<Boolean>

    // Reading Progress
    @Query("SELECT * FROM reading_progress WHERE id = 1")
    fun getReadingProgress(): Flow<ReadingProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReadingProgress(progress: ReadingProgressEntity)

    // Memorization
    @Query("SELECT * FROM memorization_items ORDER BY surahNumber ASC")
    fun getAllMemorizationItems(): Flow<List<MemorizationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemorizationItem(item: MemorizationEntity)

    @Update
    suspend fun updateMemorizationItem(item: MemorizationEntity)

    @Delete
    suspend fun deleteMemorizationItem(item: MemorizationEntity)
}
