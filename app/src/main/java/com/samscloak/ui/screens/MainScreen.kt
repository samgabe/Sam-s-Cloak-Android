package com.samscloak.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.samscloak.ui.components.SamcloakTopBar

// Sample data class for demonstration
data class DemoItem(val title: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    Scaffold(
        topBar = { SamcloakTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Sample list of items
            val items = listOf(
                DemoItem("Card 1", "This is the first card."),
                DemoItem("Card 2", "This is the second card."),
                DemoItem("Card 3", "This is the third card.")
            )
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        elevation = CardDefaults.elevatedCardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = item.title)
                            Text(text = item.description)
                        }
                    }
                }
            }
        }
    }
}
