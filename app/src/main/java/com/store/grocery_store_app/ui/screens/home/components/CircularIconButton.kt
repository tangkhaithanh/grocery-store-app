package com.store.grocery_store_app.ui.screens.home.components
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.ui.theme.DeepTeal

/**
 * A reusable circular icon button component
 *
 * @param icon The icon to display
 * @param contentDescription Description for accessibility
 * @param onClick Callback when button is clicked
 * @param tint Color tint for the icon
 * @param modifier Optional modifier for customizing the component
 */
@Composable
fun CircularIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit = {},
    tint: Color = DeepTeal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint
            )
        }
    }
}