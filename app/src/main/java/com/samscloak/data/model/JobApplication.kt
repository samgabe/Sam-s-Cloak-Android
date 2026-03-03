package com.samscloak.data.model

import com.google.gson.annotations.SerializedName

data class JobApplication(
    @SerializedName("id") val id: Int,
    @SerializedName("job_title") val jobTitle: String,
    @SerializedName("company_name") val companyName: String,
    @SerializedName("job_description") val jobDescription: String?,
    @SerializedName("job_url") val jobUrl: String?,
    @SerializedName("location") val location: String?,
    @SerializedName("salary_range") val salaryRange: String?,
    @SerializedName("remote_type") val remoteType: String?,
    @SerializedName("status") val status: ApplicationStatus,
    @SerializedName("match_score") val matchScore: Float?,
    @SerializedName("ai_analysis") val aiAnalysis: AiAnalysis?,
    @SerializedName("missing_keywords") val missingKeywords: List<String>?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

enum class ApplicationStatus {
    @SerializedName("SAVED") SAVED,
    @SerializedName("ANALYZING") ANALYZING,
    @SerializedName("ANALYZED") ANALYZED,
    @SerializedName("APPLIED") APPLIED,
    @SerializedName("INTERVIEWING") INTERVIEWING,
    @SerializedName("REJECTED") REJECTED,
    @SerializedName("ACCEPTED") ACCEPTED
}

data class AiAnalysis(
    @SerializedName("strengths") val strengths: List<String>?,
    @SerializedName("gaps") val gaps: List<String>?,
    @SerializedName("recommendations") val recommendations: List<String>?,
    @SerializedName("key_requirements") val keyRequirements: List<String>?
)

data class TailoredResumeResponse(
    @SerializedName("tailored_resume") val tailoredResume: String,
    @SerializedName("document_id") val documentId: Int
)

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserProfile(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("full_name") val fullName: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("master_resume") val masterResume: Map<String, Any>?
)
