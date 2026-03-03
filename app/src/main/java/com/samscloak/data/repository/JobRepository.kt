package com.samscloak.data.repository

import com.samscloak.data.model.JobApplication
import com.samscloak.data.model.ApplicationStatus
import com.samscloak.network.ApiService
import com.samscloak.network.JobApplicationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class JobResult<out T> {
    data class Success<T>(val data: T) : JobResult<T>()
    data class Error(val message: String) : JobResult<Nothing>()
    object Loading : JobResult<Nothing>()
}

@Singleton
class JobRepository @Inject constructor(
    private val apiService: ApiService
) {
    
    suspend fun getApplications(
        skip: Int = 0,
        limit: Int = 100,
        status: String? = null
    ): JobResult<List<JobApplication>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getApplications(skip, limit, status)
                
                if (response.isSuccessful && response.body() != null) {
                    val applications = response.body()!!.map { it.toJobApplication() }
                    JobResult.Success(applications)
                } else {
                    JobResult.Error("Failed to load applications: ${response.message()}")
                }
            } catch (e: Exception) {
                JobResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    suspend fun getApplication(id: Int): JobResult<JobApplication> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getApplication(id)
                
                if (response.isSuccessful && response.body() != null) {
                    JobResult.Success(response.body()!!.toJobApplication())
                } else {
                    JobResult.Error("Failed to load application: ${response.message()}")
                }
            } catch (e: Exception) {
                JobResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    suspend fun analyzeApplication(id: Int): JobResult<JobApplication> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.analyzeApplication(id)
                
                if (response.isSuccessful && response.body() != null) {
                    JobResult.Success(response.body()!!.toJobApplication())
                } else {
                    JobResult.Error("Failed to analyze application: ${response.message()}")
                }
            } catch (e: Exception) {
                JobResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    private fun JobApplicationResponse.toJobApplication(): JobApplication {
        return JobApplication(
            id = this.id,
            jobTitle = this.job_title,
            companyName = this.company_name,
            jobDescription = this.job_description,
            jobUrl = this.job_url,
            location = this.location,
            salaryRange = this.salary_range,
            remoteType = this.remote_type,
            status = mapStatus(this.status),
            matchScore = this.match_score,
            aiAnalysis = this.ai_analysis?.let {
                com.samscloak.data.model.AiAnalysis(
                    strengths = it.strengths,
                    gaps = it.gaps,
                    recommendations = it.recommendations,
                    keyRequirements = it.key_requirements
                )
            },
            missingKeywords = this.missing_keywords,
            createdAt = this.created_at,
            updatedAt = this.updated_at
        )
    }
    
    private fun mapStatus(status: String): ApplicationStatus {
        return when (status.uppercase()) {
            "SAVED" -> ApplicationStatus.SAVED
            "ANALYZING" -> ApplicationStatus.ANALYZING
            "ANALYZED" -> ApplicationStatus.ANALYZED
            "APPLIED" -> ApplicationStatus.APPLIED
            "INTERVIEWING" -> ApplicationStatus.INTERVIEWING
            "REJECTED" -> ApplicationStatus.REJECTED
            "ACCEPTED" -> ApplicationStatus.ACCEPTED
            else -> ApplicationStatus.SAVED
        }
    }
}
