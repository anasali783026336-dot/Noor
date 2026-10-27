package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val ayahText: String,
    val pageNumber: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_progress")
data class ReadingProgressEntity(
    @PrimaryKey val id: Int = 1, // Single record for active reading
    val lastSurahNumber: Int = 1,
    val lastSurahName: String = "الفاتحة",
    val lastAyahNumber: Int = 1,
    val lastPageNumber: Int = 1,
    val totalVersesRead: Int = 0,
    val dailyPagesGoal: Int = 4,
    val todayPagesRead: Int = 0,
    val streakDays: Int = 3,
    val lastReadDateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memorization_items")
data class MemorizationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val surahName: String,
    val fromAyah: Int,
    val toAyah: Int,
    val isMemorized: Boolean = false,
    val reviewCount: Int = 0,
    val masteryLevel: Int = 0, // 0 to 100%
    val nextReviewTimestamp: Long = System.currentTimeMillis(),
    val lastReviewTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ayah_notes")
data class AyahNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val ayahText: String,
    val noteText: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ayah_collections")
data class AyahCollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titleArabic: String,
    val description: String,
    val colorHex: String = "#064E3B",
    val ayahsCount: Int = 0,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "collection_ayah_items")
data class CollectionAyahItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collectionId: Int,
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val ayahText: String,
    val pageNumber: Int
)

@Entity(tableName = "khatma_plans")
data class KhatmaPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetDays: Int,
    val currentPage: Int = 1,
    val totalPages: Int = 604,
    val pagesPerDay: Int = 20,
    val startDateTimestamp: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false
)
