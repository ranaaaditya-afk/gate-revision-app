package com.gaterevision.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gaterevision.app.ui.components.SubjectDropdown
import com.gaterevision.app.ui.viewmodel.DEFAULT_SUBJECTS
import com.gaterevision.app.ui.viewmodel.TopicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTopicScreen(
    viewModel: TopicViewModel,
    onDone: () -> Unit
) {
    val availableSubjects by viewModel.availableSubjects.collectAsState()

    var subject by remember { mutableStateOf(DEFAULT_SUBJECTS.first()) }
    var topicName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Topic") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubjectDropdown(
                subjects = if (availableSubjects.isEmpty()) DEFAULT_SUBJECTS else availableSubjects,
                selectedSubject = subject,
                onSubjectSelected = { subject = it }
            )

            OutlinedTextField(
                value = topicName,
                onValueChange = { topicName = it },
                label = { Text("Topic name") },
                placeholder = { Text("e.g. Maxwell's Equations") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Formulas, key concepts, PYQ years") },
                placeholder = { Text("e.g. ∇×E = -∂B/∂t · PYQ: 2019, 2021, 2023") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )

            Button(
                onClick = {
                    viewModel.addTopic(subject, topicName, notes)
                    onDone()
                },
                enabled = subject.isNotBlank() && topicName.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Topic")
            }
        }
    }
}
