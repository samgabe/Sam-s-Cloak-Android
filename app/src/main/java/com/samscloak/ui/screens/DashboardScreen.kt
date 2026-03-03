package com.samscloak.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.samscloak.data.model.JobApplication
import com.samscloak.ui.theme.*
import com.samscloak.ui.viewmodel.DashboardViewModel
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val uiState by viewModel.uiState.collectAsState()
    val stats = viewModel.getStatistics()
    val listState = rememberLazyListState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Animated gradient background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            BackgroundLight,
                            SurfaceVariantLight,
                            BackgroundLight
                        )
                    )
                )
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Enhanced Header Section
                item {
                    EnhancedHeaderSection(
                        stats = stats,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onSearchClear = { searchQuery = "" }
                    )
                }

                // Quick Actions
                item {
                    QuickActionsSection()
                }

                // Filter Tabs
                item {
                    EnhancedFilterTabs(
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }

                // Job Applications List
                val filteredJobs = when (selectedTab) {
                    0 -> uiState.applications
                    1 -> uiState.applications.filter { it.status.name == "active" }
                    2 -> uiState.applications.filter { it.status.name == "applied" }
                    3 -> uiState.applications.filter { it.status.name == "interviewing" }
                    else -> uiState.applications
                }

                val searchedJobs = if (searchQuery.isBlank()) {
                    filteredJobs
                } else {
                    filteredJobs.filter { 
                        it.jobTitle.contains(searchQuery, ignoreCase = true) ||
                        it.companyName.contains(searchQuery, ignoreCase = true)
                    }
                }

                items(searchedJobs) { job ->
                    key(job.id) {
                        EnhancedJobCard(
                            job = job,
                            onClick = { /* Handle click */ }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                if (searchedJobs.isEmpty()) {
                    item {
                        EnhancedEmptyStateCard(
                            message = if (searchQuery.isNotBlank()) "No jobs found matching \"$searchQuery\"" 
                            else "No jobs yet. Add your first application!"
                        )
                    }
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            EnhancedFloatingActionButton(
                onClick = { /* Add job */ },
                icon = Icons.Default.Add,
                text = "Add Job"
            )
        }
    }
}

@Composable
fun EnhancedHeaderSection(
    stats: Map<String, Int>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSearchClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SamsCloak AI",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Text(
                    text = "Your Career Intelligence Platform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            
            EnhancedProfileAvatar()
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Cards
        EnhancedStatsCards(stats = stats)

        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        EnhancedSearchBar(
            query = searchQuery,
            onQueryChange = onSearchChange,
            onClear = onSearchClear
        )
    }
}

@Composable
fun EnhancedProfileAvatar() {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Primary, Secondary))),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Profile",
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun EnhancedStatsCards(stats: Map<String, Int>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EnhancedStatCard(
            title = "Total",
            value = stats["total"] ?: 0,
            icon = Icons.Default.Work,
            color = Primary,
            modifier = Modifier.weight(1f)
        )
        EnhancedStatCard(
            title = "Applied",
            value = stats["applied"] ?: 0,
            icon = Icons.Default.Send,
            color = Success,
            modifier = Modifier.weight(1f)
        )
        EnhancedStatCard(
            title = "Interview",
            value = stats["interviewing"] ?: 0,
            icon = Icons.Default.Event,
            color = Info,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EnhancedStatCard(
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = color.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun EnhancedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search jobs or companies...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear",
                        tint = TextSecondary
                    )
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = BorderLight
        ),
        singleLine = true
    )
}

@Composable
fun QuickActionsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Add,
                title = "Add Job",
                color = Primary,
                onClick = { /* Add job */ },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Default.Analytics,
                title = "Analyze All",
                color = Secondary,
                onClick = { /* Analyze all */ },
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Default.FileDownload,
                title = "Export",
                color = Tertiary,
                onClick = { /* Export */ },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedFilterTabs(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val tabs = listOf("All", "Active", "Applied", "Interviewing")
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color.Transparent,
            contentColor = Primary
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelected(index) },
                    text = {
                        Text(
                            text = tab,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun EnhancedJobCard(
    job: JobApplication,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = Primary.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = job.jobTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = job.companyName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    if (job.location?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = job.location,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary
                        )
                    }
                }
                
                // Match Score Badge
                job.matchScore?.let { score ->
                    EnhancedMatchScoreBadge(score = score)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status and Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedStatusChip(status = job.status.name)
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { /* Analyze */ },
                        modifier = Modifier
                            .size(36.dp)
                            .background(InfoContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analyze",
                            tint = Info,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    
                    IconButton(
                        onClick = { /* Tailor */ },
                        modifier = Modifier
                            .size(36.dp)
                            .background(SuccessContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Tailor",
                            tint = Success,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedMatchScoreBadge(score: Float) {
    val color = when {
        score >= 90 -> ExcellentMatch
        score >= 70 -> GoodMatch
        score >= 50 -> AverageMatch
        else -> PoorMatch
    }
    
    val backgroundColor = when {
        score >= 90 -> SuccessContainer
        score >= 70 -> InfoContainer
        score >= 50 -> WarningContainer
        else -> ErrorContainer
    }
    
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Text(
            text = "${score.toInt()}%",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun EnhancedStatusChip(status: String) {
    val (color, backgroundColor, icon) = when (status.lowercase()) {
        "active" -> Triple(Info, InfoContainer, Icons.Default.Schedule)
        "applied" -> Triple(Success, SuccessContainer, Icons.Default.Send)
        "interviewing" -> Triple(Warning, WarningContainer, Icons.Default.VideoCall)
        "rejected" -> Triple(Error, ErrorContainer, Icons.Default.Close)
        "offer" -> Triple(ExcellentMatch, SuccessContainer, Icons.Default.CardGiftcard)
        else -> Triple(TextSecondary, SurfaceVariantLight, Icons.Default.HelpOutline)
    }
    
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            
            Spacer(modifier = Modifier.width(6.dp))
            
            Text(
                text = status.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}

@Composable
fun EnhancedEmptyStateCard(
    message: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantLight
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.WorkOutline,
                contentDescription = null,
                tint = TextTertiary,
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EnhancedFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String
) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        containerColor = Primary,
        elevation = FloatingActionButtonDefaults.elevation(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White
        )
    }
}
