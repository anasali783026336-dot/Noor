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

    // Ayah Personal Notes & Reflections
    @Query("SELECT * FROM ayah_notes WHERE surahNumber = :surahNumber AND ayahNumber = :ayahNumber ORDER BY timestamp DESC")
    fun getNotesForAyah(surahNumber: Int, ayahNumber: Int): Flow<List<AyahNoteEntity>>

    @Query("SELECT * FROM ayah_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<AyahNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAyahNote(note: AyahNoteEntity)

    @Delete
    suspend fun deleteAyahNote(note: AyahNoteEntity)

    // Ayah Collections / Groups (آيات السكينة، آيات الشفاء، إلخ)
    @Query("SELECT * FROM ayah_collections ORDER BY createdTimestamp DESC")
    fun getAllCollections(): Flow<List<AyahCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: AyahCollectionEntity): Long

    @Delete
    suspend fun deleteCollection(collection: AyahCollectionEntity)

    @Query("SELECT * FROM collection_ayah_items WHERE collectionId = :collectionId")
    fun getItemsForCollection(collectionId: Int): Flow<List<CollectionAyahItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionItem(item: CollectionAyahItemEntity)

    @Delete
    suspend fun deleteCollectionItem(item: CollectionAyahItemEntity)

    // Khatma Plans
    @Query("SELECT * FROM khatma_plans ORDER BY startDateTimestamp DESC")
    fun getAllKhatmaPlans(): Flow<List<KhatmaPlanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKhatmaPlan(plan: KhatmaPlanEntity)

    @Update
    suspend fun updateKhatmaPlan(plan: KhatmaPlanEntity)

    @Delete
    suspend fun deleteKhatmaPlan(plan: KhatmaPlanEntity)
}
