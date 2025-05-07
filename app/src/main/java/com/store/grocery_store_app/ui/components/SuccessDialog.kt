package com.store.grocery_store_app.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SuccessDialog(
    title: String,
    content: String,
    clearError : () -> Unit = {},
    onDismissRequest: () -> Unit = {},
    confirmButtonRequest: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = { onDismissRequest() },
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = Color.Green)
                Spacer(modifier = Modifier.width(8.dp))
                Text(content)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                confirmButtonRequest()
                clearError()
            }) {
                Text("OK", color = MaterialTheme.colorScheme.primary)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}