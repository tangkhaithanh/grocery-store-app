package com.store.grocery_store_app.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    title: String,
    content: String,
    clearError : () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = clearError,
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = { Text(content, style = MaterialTheme.typography.bodyMedium) },
        confirmButton = {
            TextButton(onClick = clearError) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}