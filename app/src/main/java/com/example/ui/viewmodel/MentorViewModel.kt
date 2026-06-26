package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Interaction
import com.example.data.model.UserProfile
import com.example.data.repository.MentorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MentorViewModel(private val repository: MentorRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val interactions: StateFlow<List<Interaction>> = repository.allInteractions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _latestAnalysis = MutableStateFlow<Interaction?>(null)
    val latestAnalysis: StateFlow<Interaction?> = _latestAnalysis.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearErrorMessage() {
        _errorMessage.value = null
    }

    fun clearLatestAnalysis() {
        _latestAnalysis.value = null
    }

    fun analyzeSituation(situation: String, isVoice: Boolean) {
        if (situation.trim().isEmpty()) return
        viewModelScope.launch {
            _isAnalyzing.value = true
            _errorMessage.value = null
            val result = repository.analyzeSituation(situation, isVoice)
            _isAnalyzing.value = false
            result.onSuccess { interaction ->
                _latestAnalysis.value = interaction
            }.onFailure { exception ->
                _errorMessage.value = exception.message ?: "Ocorreu um erro desconhecido durante a análise."
            }
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.clearAll()
            _latestAnalysis.value = null
            _errorMessage.value = null
        }
    }

    // Class factory
    class Factory(private val repository: MentorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MentorViewModel::class.java)) {
                return MentorViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
