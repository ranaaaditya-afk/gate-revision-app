package com.gaterevision.app.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import com.gaterevision.app.ui.components.TopicCard
import com.gaterevision.app.ui.viewmodel.TopicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: TopicViewModel,
    onAddTopic: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Due Today", "All Topics")

    Scaffold(
        topBar = { TopAppBar(title = { Text("GATE Revision") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTopic) {
                Icon(Icons.Filled.Add, contentDescription = "Add topic")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> DueTodayTab(viewModel)
                1 -> AllTopicsTab(viewModel)
            }
        }
    }
}

@Composable
private fun DueTodayTab(viewModel: TopicViewModel) {
    val dueTopics by viewModel.dueTopics.collectAsState()

    if (dueTopics.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp)
        ) {
            Text("Nothing due today — nice work! Add a new topic or check back tomorrow.")
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(dueTopics, key = { it.id }) { topic ->
            TopicCard(
                topic = topic,
                onMarkRevised = { viewModel.markRevised(it) },
                onDelete = { viewModel.deleteTopic(it) },
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AllTopicsTab(viewModel: TopicViewModel) {
    val topics by viewModel.filteredTopics.collectAsState()
    val subjects by viewModel.availableSubjects.collectAsState()
    val query by viewModel.searchQuery.collectAsState()
    val activeSubjectFilter by viewModel.subjectFilter.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.setSearchQuery(it) },
            label = { Text("Search topics or notes") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true
        )

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = activeSubjectFilter == null,
                    onClick = { viewModel.setSubjectFilter(null) },
                    label = { Text("All") }
                )
            }
            items(subjects) { subject ->
                FilterChip(
                    selected = activeSubjectFilter == subject,
                    onClick = {
                        viewModel.setSubjectFilter(if (activeSubjectFilter == subject) null else subject)
                    },
                    label = { Text(subject) }
                )
            }
        }

        if (topics.isEmpty()) {
            Column(modifier = Modifier.fillMaxSize().padding(32.dp)) {
                Text("No topics match. Try clearing the search or filter, or add a new topic.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(topics, key = { it.id }) { topic ->
                    TopicCard(
                        topic = topic,
                        onMarkRevised = { viewModel.markRevised(it) },
                        onDelete = { viewModel.deleteTopic(it) },
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
            }
        }
    }
}
