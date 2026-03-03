package com.samscloak.data.repository

import android.content.Context
import com.samscloak.network.ApiService
import com.samscloak.network.LoginRequest
import com.samscloak.network.LoginResponse
import com.samscloak.network.RegisterRequest
import com.samscloak.network.RetrofitClient
import com.samscloak.network.UserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
    object Loading : AuthResult<Nothing>()
}

@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    
    suspend fun login(email: String, password: String): AuthResult<LoginResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    // Save token
                    RetrofitClient.saveToken(context, loginResponse.access_token)
                    AuthResult.Success(loginResponse)
                } else {
                    val errorMessage = when (response.code()) {
                        401 -> "Invalid email or password"
                        404 -> "User not found"
                        422 -> "Invalid input data"
                        500 -> "Server error. Please try again later"
                        else -> "Login failed: ${response.message()}"
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                AuthResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    suspend fun register(
        email: String,
        password: String,
        fullName: String?
    ): AuthResult<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(
                    RegisterRequest(email, password, fullName)
                )
                
                if (response.isSuccessful && response.body() != null) {
                    AuthResult.Success(response.body()!!)
                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Email already registered"
                        422 -> "Invalid input data"
                        500 -> "Server error. Please try again later"
                        else -> "Registration failed: ${response.message()}"
                    }
                    AuthResult.Error(errorMessage)
                }
            } catch (e: Exception) {
                AuthResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    suspend fun getCurrentUser(): AuthResult<UserResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCurrentUser()
                
                if (response.isSuccessful && response.body() != null) {
                    AuthResult.Success(response.body()!!)
                } else {
                    AuthResult.Error("Failed to get user: ${response.message()}")
                }
            } catch (e: Exception) {
                AuthResult.Error("Network error: ${e.localizedMessage ?: "Unknown error"}")
            }
        }
    }
    
    fun logout() {
        RetrofitClient.clearToken(context)
    }
    
    fun isLoggedIn(): Boolean {
        return RetrofitClient.loadToken(context) != null
    }
    
    fun getToken(): String? {
        return RetrofitClient.loadToken(context)
    }
}
