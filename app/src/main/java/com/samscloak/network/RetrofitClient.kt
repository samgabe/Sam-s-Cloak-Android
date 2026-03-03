package com.samscloak.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Retrofit client singleton for API communication
 */
object RetrofitClient {
    
    // For physical device - use your computer's IP address
    // Make sure backend is running with: uvicorn app.main:app --host 0.0.0.0 --port 8000
    private const val BASE_URL = "http://192.168.126.71:8000/"  // Updated to correct backend IP
    private const val TIMEOUT_SECONDS = 30L
    
    private var authToken: String? = null
    
    /**
     * Set authentication token
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }
    
    /**
     * Get authentication token
     */
    fun getAuthToken(): String? = authToken
    
    /**
     * Auth interceptor to add Bearer token to requests
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        chain.proceed(requestBuilder.build())
    }
    
    /**
     * Logging interceptor for debugging
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    /**
     * OkHttp client with interceptors
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()
    
    /**
     * Retrofit instance
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    /**
     * API service instance
     */
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    /**
     * Save token to SharedPreferences
     */
    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("samscloak_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("auth_token", token).apply()
        setAuthToken(token)
    }
    
    /**
     * Load token from SharedPreferences
     */
    fun loadToken(context: Context): String? {
        val prefs = context.getSharedPreferences("samscloak_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)
        setAuthToken(token)
        return token
    }
    
    /**
     * Clear token from SharedPreferences
     */
    fun clearToken(context: Context) {
        val prefs = context.getSharedPreferences("samscloak_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("auth_token").apply()
        setAuthToken(null)
    }
}
