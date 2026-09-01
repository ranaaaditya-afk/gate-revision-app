package com.gaterevision.app.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query("SELECT * FROM topics ORDER BY createdAt DESC")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE nextReviewDate <= :endOfTodayMillis AND cycleCompleted = 0 ORDER BY nextReviewDate ASC")
    fun getDueTopics(endOfTodayMillis: Long): Flow<List<Topic>>

    @Query("SELECT DISTINCT subject FROM topics ORDER BY subject ASC")
    fun getUsedSubjects(): Flow<List<String>>

    @Insert
    suspend fun insert(topic: Topic): Long

    @Update
    suspend fun update(topic: Topic)

    @Delete
    suspend fun delete(topic: Topic)

    // Used by the daily notification worker — a plain one-shot count, not a Flow.
    @Query("SELECT COUNT(*) FROM topics WHERE nextReviewDate <= :endOfTodayMillis AND cycleCompleted = 0")
    suspend fun getDueCount(endOfTodayMillis: Long): Int
}
