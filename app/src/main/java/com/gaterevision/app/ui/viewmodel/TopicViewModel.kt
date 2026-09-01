package com.gaterevision.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gaterevision.app.data.AppDatabase
import com.gaterevision.app.data.Topic
import com.gaterevision.app.data.TopicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** The fixed list of built-in GATE ECE subjects shown in the dropdown. */
val DEFAULT_SUBJECTS = listOf(
    "Engineering Mathematics",
    "Digital Circuits",
    "Signals & Systems",
    "Electromagnetics",
    "Network Theory",
    "Control Systems",
    "Electronic Devices",
    "Analog Circuits",
    "Communications",
    "General Aptitude"
)

class TopicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TopicRepository

    init {
        val dao = AppDatabase.getInstance(application).topicDao()
        repository = TopicRepository(dao)
    }

    val allTopics: StateFlow<List<Topic>> = repository.getAllTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val dueTopics: StateFlow<List<Topic>> = repository.getDueTopics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val customSubjectsFromDb: StateFlow<List<String>> = repository.getUsedSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Built-in subjects plus any custom subject the user has typed in before. */
    val availableSubjects: StateFlow<List<String>> = customSubjectsFromDb
        .combine(MutableStateFlow(DEFAULT_SUBJECTS)) { custom, defaults ->
            (defaults + custom).distinct()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DEFAULT_SUBJECTS)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _subjectFilter = MutableStateFlow<String?>(null) // null = all subjects
    val subjectFilter: StateFlow<String?> = _subjectFilter

    val filteredTopics: StateFlow<List<Topic>> = combine(
        allTopics, _searchQuery, _subjectFilter
    ) { topics, query, subject ->
        topics.filter { topic ->
            val matchesQuery = query.isBlank() ||
                topic.topicName.contains(query, ignoreCase = true) ||
                topic.notes.contains(query, ignoreCase = true)
            val matchesSubject = subject == null || topic.subject == subject
            matchesQuery && matchesSubject
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: String?) {
        _subjectFilter.value = subject
    }

    fun addTopic(subject: String, topicName: String, notes: String) {
        if (subject.isBlank() || topicName.isBlank()) return
        viewModelScope.launch {
            repository.addTopic(subject.trim(), topicName.trim(), notes.trim())
        }
    }

    fun markRevised(topic: Topic) {
        viewModelScope.launch {
            repository.markRevised(topic)
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }
}
