package com.gaterevision.app.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gaterevision.app.data.REVIEW_INTERVALS_DAYS
import com.gaterevision.app.data.Topic
import com.gaterevision.app.ui.theme.DoneGreen
import com.gaterevision.app.ui.theme.DueOrange
import com.gaterevision.app.ui.theme.OverdueRed
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun TopicCard(
    topic: Topic,
    onMarkRevised: (Topic) -> Unit,
    onDelete: (Topic) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = topic.topicName, style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = topic.subject,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            StatusChip(topic)

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                if (topic.notes.isNotBlank()) {
                    Text(
                        text = "Formulas / Notes / PYQs",
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = topic.notes, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Text(
                    text = "Revised ${topic.timesRevised} time(s) · " +
                        (if (topic.cycleCompleted) "Full cycle complete"
                         else "Next gap: ${REVIEW_INTERVALS_DAYS[topic.intervalIndex]} day(s)"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { onMarkRevised(topic) },
                        enabled = !topic.cycleCompleted
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.height(0.dp))
                        Text("  Mark Revised")
                    }
                    IconButton(onClick = { onDelete(topic) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete topic")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusChip(topic: Topic) {
    val now = System.currentTimeMillis()
    val (label, color) = when {
        topic.cycleCompleted -> "Cycle complete" to DoneGreen
        topic.nextReviewDate <= now -> "Due now" to OverdueRed
        else -> {
            val daysLeft = TimeUnit.MILLISECONDS.toDays(topic.nextReviewDate - now) + 1
            "Due in $daysLeft day(s) · ${formatDate(topic.nextReviewDate)}" to DueOrange
        }
    }
    AssistChip(
        onClick = {},
        label = { Text(label, color = Color.White) },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = color
        )
    )
}

private fun formatDate(millis: Long): String =
    SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(millis))
