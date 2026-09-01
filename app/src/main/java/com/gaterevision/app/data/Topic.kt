package com.gaterevision.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * The spaced-repetition schedule: review after 1, 3, 7, 30, then 60 days.
 * intervalIndex points to which of these gaps applies NEXT.
 */
val REVIEW_INTERVALS_DAYS = listOf(1, 3, 7, 30, 60)

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val subject: String,
    val topicName: String,
    val notes: String,

    // Epoch millis for when this topic was first added.
    val createdAt: Long,

    // Epoch millis for when this topic is next due for revision.
    val nextReviewDate: Long,

    // Index into REVIEW_INTERVALS_DAYS for the interval that was just used
    // (or is about to be used) to compute nextReviewDate.
    val intervalIndex: Int = 0,

    // How many times "Mark Revised" has been tapped for this topic.
    val timesRevised: Int = 0,

    // True once all 5 intervals (up to 60 days) have been completed.
    val cycleCompleted: Boolean = false
)
