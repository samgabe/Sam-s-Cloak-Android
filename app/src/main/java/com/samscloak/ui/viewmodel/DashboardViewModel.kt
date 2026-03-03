package com.samscloak.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samscloak.data.model.JobApplication
import com.samscloak.data.repository.JobRepository
import com.samscloak.data.repository.JobResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val applications: List<JobApplication> = emptyList(),
    val error: String? = null,
    val isRefreshing: Boolean = false
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()
    
    init {
        loadApplications()
    }
    
    fun loadApplications(status: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            when (val result = jobRepository.getApplications(status = status)) {
                is JobResult.Success -> {
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        applications = result.data,
                        error = null
                    )
                }
                is JobResult.Error -> {
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        applications = emptyList(),
                        error = result.message
                    )
                }
                is JobResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }
        }
    }
    
    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true)
            
            when (val result = jobRepository.getApplications()) {
                is JobResult.Success -> {
                    _uiState.value = DashboardUiState(
                        isLoading = false,
                        isRefreshing = false,
                        applications = result.data,
                        error = null
                    )
                }
                is JobResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isRefreshing = false,
                        error = result.message
                    )
                }
                is JobResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isRefreshing = true)
                }
            }
        }
    }
    
    fun getStatistics(): Map<String, Int> {
        val applications = _uiState.value.applications
        return mapOf(
            "total" to applications.size,
            "applied" to applications.count { it.status.name == "APPLIED" },
            "interviewing" to applications.count { it.status.name == "INTERVIEWING" },
            "offers" to applications.count { it.status.name == "ACCEPTED" }
        )
    }
}
