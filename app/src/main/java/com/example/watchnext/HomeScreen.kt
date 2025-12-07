package com.example.watchnext

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val moviesState = remember { mutableStateOf<List<ImdbTitle>?>(null) }
    val errorState = remember { mutableStateOf<String?>(null) }

    // Call API once when screen starts
    LaunchedEffect(Unit) {
        try {
            val response = withContext(Dispatchers.IO) {
                MovieApi.service.getCastTitles("nm0000190")
            }
            moviesState.value = response              // 👈 NOT response.titles
        } catch (e: Exception) {
            errorState.value = e.localizedMessage ?: "Unknown error"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shared Watchlist") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: navigate to AddMovie screen later
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add movie")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                errorState.value != null -> {
                    Text(
                        text = "Error: ${errorState.value}",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                moviesState.value == null -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                moviesState.value!!.isEmpty() -> {
                    Text(
                        text = "No movies yet.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(moviesState.value!!) { movie ->
                            MovieRow(movie)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieRow(movie: ImdbTitle) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Poster (or placeholder)
            if (movie.primaryImage != null) {
                AsyncImage(
                    model = movie.primaryImage,
                    contentDescription = movie.primaryTitle,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = movie.primaryTitle ?: "(No title)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val yearText = movie.startYear?.toString() ?: "Unknown year"
                Text(
                    text = yearText,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = movie.description ?: "",
                    maxLines = 2,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            // Right side: rating / votes & status chip
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(64.dp)
            ) {
                Text(
                    text = "★ ${movie.averageRating ?: 0.0}",
                    fontSize = 12.sp
                )
                Text(
                    text = "${movie.numVotes ?: 0} votes",
                    fontSize = 10.sp
                )

                Spacer(Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "To Watch",   // later we can toggle to "Watched"
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}