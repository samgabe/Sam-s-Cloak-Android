package com.samscloak

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.samscloak.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * Main activity for SamsCloak application
 * 
 * Displays list of job applications and provides navigation
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: JobApplicationAdapter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Load auth token
        RetrofitClient.loadToken(this)
        
        // Check if should show login
        if (intent.getBooleanExtra("show_login", false)) {
            // Show login screen
            showLoginScreen()
            return
        }
        
        // Setup RecyclerView
        setupRecyclerView()
        
        // Load applications
        loadApplications()
        
        // Check if should show specific application
        val applicationId = intent.getIntExtra("application_id", -1)
        if (applicationId != -1 && intent.getBooleanExtra("show_details", false)) {
            showApplicationDetails(applicationId)
        }
    }
    
    /**
     * Setup RecyclerView for job applications
     */
    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = JobApplicationAdapter { application ->
            showApplicationDetails(application.id)
        }
        
        recyclerView.adapter = adapter
    }
    
    /**
     * Load job applications from API
     */
    private fun loadApplications() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getApplications()
                
                if (response.isSuccessful) {
                    val applications = response.body() ?: emptyList()
                    adapter.submitList(applications)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to load applications",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Show application details
     */
    private fun showApplicationDetails(applicationId: Int) {
        // TODO: Implement details screen
        Toast.makeText(this, "Opening application $applicationId", Toast.LENGTH_SHORT).show()
    }
    
    /**
     * Show login screen
     */
    private fun showLoginScreen() {
        // TODO: Implement login screen
        Toast.makeText(this, "Please login", Toast.LENGTH_SHORT).show()
    }
}
