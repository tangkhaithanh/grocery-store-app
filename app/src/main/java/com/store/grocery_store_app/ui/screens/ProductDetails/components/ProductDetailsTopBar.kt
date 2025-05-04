package com.store.grocery_store_app.ui.screens.ProductDetails.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.ui.theme.ErrorLight
import com.store.grocery_store_app.ui.theme.Gray600
/**
 * Top app bar with navigation and favorite button
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsTopBar(
    isFavorite: Boolean,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    TopAppBar(
        title = { Text("Chi tiết sản phẩm") },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        },
        actions = {
            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) ErrorLight else Color.Gray
                )
            }
        }
    )
}

/**
 * Error content to display when product loading fails
 */
@Composable
fun ErrorContent(
    error: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Không thể tải thông tin sản phẩm",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Gray600
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNavigateBack) {
            Text("Quay lại")
        }
    }
}