package com.gaterevision.app.data

import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class TopicRepository(private val dao: TopicDao) {

    fun getAllTopics(): Flow<List<Topic>> = dao.getAllTopics()

    fun getUsedSubjects(): Flow<List<String>> = dao.getUsedSubjects()

    fun getDueTopics(): Flow<List<Topic>> = dao.getDueTopics(endOfToday())

    suspend fun addTopic(subject: String, topicName: String, notes: String) {
        val now = System.currentTimeMillis()
        val firstIntervalDays = REVIEW_INTERVALS_DAYS[0]
        val topic = Topic(
            subject = subject,
            topicName = topicName,
            notes = notes,
            createdAt = now,
            nextReviewDate = now + firstIntervalDays * DAY_MILLIS,
            intervalIndex = 0,
            timesRevised = 0,
            cycleCompleted = false
        )
        dao.insert(topic)
    }

    suspend fun deleteTopic(topic: Topic) {
        dao.delete(topic)
    }

    /**
     * Called when the user taps "Mark Revised" on a topic.
     * Advances it to the next spaced-repetition interval, or marks the
     * cycle complete once the 60-day review has been done.
     */
    suspend fun markRevised(topic: Topic) {
        val nextIndex = topic.intervalIndex + 1
        val updated = if (nextIndex >= REVIEW_INTERVALS_DAYS.size) {
            topic.copy(
                timesRevised = topic.timesRevised + 1,
                cycleCompleted = true
            )
        } else {
            val gapDays = REVIEW_INTERVALS_DAYS[nextIndex]
            topic.copy(
                intervalIndex = nextIndex,
                nextReviewDate = System.currentTimeMillis() + gapDays * DAY_MILLIS,
                timesRevised = topic.timesRevised + 1
            )
        }
        dao.update(updated)
    }

    suspend fun getDueCount(): Int = dao.getDueCount(endOfToday())

    companion object {
        const val DAY_MILLIS = 24L * 60 * 60 * 1000

        fun endOfToday(): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, 23)
            cal.set(Calendar.MINUTE, 59)
            cal.set(Calendar.SECOND, 59)
            cal.set(Calendar.MILLISECOND, 999)
            return cal.timeInMillis
        }
    }
}
