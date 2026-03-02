package com.samscloak.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API service interface for SamsCloak backend
 */
interface ApiService {
    
    /**
     * Ingest job posting from screenshot
     */
    @Multipart
    @POST("api/v1/ingest")
    suspend fun ingestJobPosting(
        @Part file: MultipartBody.Part,
        @Part("token") token: RequestBody,
        @Part("job_title") jobTitle: RequestBody? = null,
        @Part("company_name") companyName: RequestBody? = null,
        @Part("location") location: RequestBody? = null,
        @Part("job_url") jobUrl: RequestBody? = null,
        @Part("salary_range") salaryRange: RequestBody? = null,
        @Part("remote_type") remoteType: RequestBody? = null
    ): Response<JobApplicationResponse>
    
    /**
     * Get all user applications
     */
    @GET("api/v1/applications")
    suspend fun getApplications(
        @Query("skip") skip: Int = 0,
        @Query("limit") limit: Int = 100,
        @Query("status") status: String? = null
    ): Response<List<JobApplicationResponse>>
    
    /**
     * Get specific application
     */
    @GET("api/v1/applications/{id}")
    suspend fun getApplication(
        @Path("id") id: Int
    ): Response<JobApplicationResponse>
    
    /**
     * Analyze job application
     */
    @POST("api/v1/applications/{id}/analyze")
    suspend fun analyzeApplication(
        @Path("id") id: Int
    ): Response<JobApplicationResponse>
    
    /**
     * Generate tailored resume
     */
    @POST("api/v1/applications/{id}/tailor-resume")
    suspend fun tailorResume(
        @Path("id") id: Int
    ): Response<TailoredResumeResponse>
    
    /**
     * Generate cover letter
     */
    @POST("api/v1/applications/{id}/cover-letter")
    suspend fun generateCoverLetter(
        @Path("id") id: Int
    ): Response<CoverLetterResponse>
    
    /**
     * Update application status
     */
    @PUT("api/v1/applications/{id}/status")
    suspend fun updateStatus(
        @Path("id") id: Int,
        @Body statusUpdate: StatusUpdateRequest
    ): Response<JobApplicationResponse>
    
    /**
     * Login user
     */
    @POST("api/v1/users/login")
    suspend fun login(
        @Body credentials: LoginRequest
    ): Response<LoginResponse>
    
    /**
     * Register new user
     */
    @POST("api/v1/users/register")
    suspend fun register(
        @Body userData: RegisterRequest
    ): Response<UserResponse>
    
    /**
     * Get current user
     */
    @GET("api/v1/users/me")
    suspend fun getCurrentUser(): Response<UserResponse>
    
    /**
     * Get application statistics
     */
    @GET("api/v1/statistics")
    suspend fun getStatistics(): Response<StatisticsResponse>
}

// Data classes for API requests and responses

data class JobApplicationResponse(
    val id: Int,
    val job_title: String,
    val company_name: String,
    val job_description: String,
    val job_url: String?,
    val location: String?,
    val salary_range: String?,
    val remote_type: String?,
    val status: String,
    val user_id: Int,
    val raw_text: String?,
    val ai_analysis: AIAnalysis?,
    val match_score: Float?,
    val missing_keywords: List<String>?,
    val created_at: String,
    val updated_at: String
)

data class AIAnalysis(
    val match_score: Float,
    val strengths: List<String>,
    val gaps: List<String>,
    val missing_keywords: List<String>,
    val experience_match: Float,
    val skills_match: Float,
    val education_match: Float,
    val recommendations: List<String>,
    val key_requirements: List<String>
)

data class TailoredResumeResponse(
    val tailored_resume: String
)

data class CoverLetterResponse(
    val cover_letter: String
)

data class StatusUpdateRequest(
    val status: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val access_token: String,
    val token_type: String,
    val user: UserResponse
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val full_name: String?
)

data class UserResponse(
    val id: Int,
    val email: String,
    val full_name: String?,
    val is_active: Boolean,
    val created_at: String,
    val updated_at: String
)

data class StatisticsResponse(
    val total_applications: Int,
    val status_breakdown: Map<String, Int>,
    val average_match_score: Float,
    val analyzed_applications: Int,
    val applied_applications: Int
)
