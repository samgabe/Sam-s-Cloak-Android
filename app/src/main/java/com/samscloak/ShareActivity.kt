package com.samscloak

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.samscloak.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

/**
 * Activity to handle Share intent for job posting screenshots
 * 
 * This activity receives shared images from other apps (e.g., screenshot sharing)
 * and uploads them to the SamsCloak backend for OCR and AI analysis.
 */
class ShareActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is authenticated
        val token = RetrofitClient.loadToken(this)
        if (token == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_LONG).show()
            // Redirect to login
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("show_login", true)
            startActivity(intent)
            finish()
            return
        }
        
        // Handle the shared image
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type?.startsWith("image/") == true) {
                    handleSharedImage(intent)
                } else {
                    Toast.makeText(this, "Invalid file type", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else -> {
                Toast.makeText(this, "Invalid intent", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
    
    /**
     * Handle shared image intent
     */
    private fun handleSharedImage(intent: Intent) {
        val imageUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        
        if (imageUri == null) {
            Toast.makeText(this, "No image found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        Toast.makeText(this, "Uploading job posting...", Toast.LENGTH_SHORT).show()
        
        // Upload image in background
        lifecycleScope.launch {
            try {
                uploadJobPosting(imageUri)
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ShareActivity,
                        "Upload failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                finish()
            }
        }
    }
    
    /**
     * Upload job posting screenshot to backend
     */
    private suspend fun uploadJobPosting(imageUri: Uri) {
        try {
            // Copy image to temporary file
            val inputStream = contentResolver.openInputStream(imageUri)
            val tempFile = File(cacheDir, "temp_job_posting.jpg")
            val outputStream = FileOutputStream(tempFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            // Prepare multipart request
            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
            
            val token = RetrofitClient.getAuthToken() ?: ""
            val tokenBody = token.toRequestBody("text/plain".toMediaTypeOrNull())
            
            // Make API call
            val response = RetrofitClient.apiService.ingestJobPosting(
                file = filePart,
                token = tokenBody
            )
            
            // Clean up temp file
            tempFile.delete()
            
            if (response.isSuccessful) {
                val application = response.body()
                runOnUiThread {
                    Toast.makeText(
                        this@ShareActivity,
                        "Job posting added successfully!",
                        Toast.LENGTH_LONG
                    ).show()
                    
                    // Open main activity with the new application
                    val intent = Intent(this@ShareActivity, MainActivity::class.java)
                    intent.putExtra("application_id", application?.id)
                    intent.putExtra("show_details", true)
                    startActivity(intent)
                    finish()
                }
            } else {
                throw Exception("Upload failed: ${response.code()}")
            }
            
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(
                    this@ShareActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            finish()
        }
    }
}
