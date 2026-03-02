package com.samscloak

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.samscloak.network.JobApplicationResponse
import java.text.SimpleDateFormat
import java.util.*

/**
 * RecyclerView adapter for job applications
 */
class JobApplicationAdapter(
    private val onItemClick: (JobApplicationResponse) -> Unit
) : ListAdapter<JobApplicationResponse, JobApplicationAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_job_application, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        itemView: View,
        private val onItemClick: (JobApplicationResponse) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvJobTitle: TextView = itemView.findViewById(R.id.tvJobTitle)
        private val tvCompany: TextView = itemView.findViewById(R.id.tvCompany)
        private val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val tvMatchScore: TextView = itemView.findViewById(R.id.tvMatchScore)
        private val tvCreatedAt: TextView = itemView.findViewById(R.id.tvCreatedAt)

        fun bind(application: JobApplicationResponse) {
            tvJobTitle.text = application.job_title
            tvCompany.text = application.company_name
            
            // Location
            if (application.location != null) {
                tvLocation.text = application.location
                tvLocation.visibility = View.VISIBLE
            } else {
                tvLocation.visibility = View.GONE
            }

            // Status
            tvStatus.text = application.status
            tvStatus.setBackgroundColor(getStatusColor(application.status))

            // Match score
            if (application.match_score != null) {
                tvMatchScore.text = "Match: ${application.match_score.toInt()}%"
                tvMatchScore.setTextColor(getScoreColor(application.match_score))
                tvMatchScore.visibility = View.VISIBLE
            } else {
                tvMatchScore.visibility = View.GONE
            }

            // Created at
            tvCreatedAt.text = formatDate(application.created_at)

            // Click listener
            itemView.setOnClickListener {
                onItemClick(application)
            }
        }

        private fun getStatusColor(status: String): Int {
            return when (status) {
                "PENDING" -> 0xFFFFA726.toInt()
                "ANALYZED" -> 0xFF42A5F5.toInt()
                "APPLIED" -> 0xFF66BB6A.toInt()
                "REJECTED" -> 0xFFEF5350.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }

        private fun getScoreColor(score: Float): Int {
            return when {
                score >= 80 -> 0xFF66BB6A.toInt()
                score >= 60 -> 0xFF42A5F5.toInt()
                score >= 40 -> 0xFFFFA726.toInt()
                else -> 0xFFEF5350.toInt()
            }
        }

        private fun formatDate(dateString: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val date = inputFormat.parse(dateString) ?: return dateString
                
                val now = Date()
                val diffMillis = now.time - date.time
                val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()

                when {
                    diffDays == 0 -> "Today"
                    diffDays == 1 -> "Yesterday"
                    diffDays < 7 -> "$diffDays days ago"
                    diffDays < 30 -> "${diffDays / 7} weeks ago"
                    else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
                }
            } catch (e: Exception) {
                dateString
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<JobApplicationResponse>() {
        override fun areItemsTheSame(
            oldItem: JobApplicationResponse,
            newItem: JobApplicationResponse
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: JobApplicationResponse,
            newItem: JobApplicationResponse
        ): Boolean {
            return oldItem == newItem
        }
    }
}
