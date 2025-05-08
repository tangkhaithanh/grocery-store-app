package com.store.grocery_store_app.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray500

@Composable
fun HeaderSection(
    isUserLoggedIn: Boolean,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit = {},
    onNavigateToSearch: () -> Unit,
    cartItemCount: Int? = null,
    locationName: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepTeal)
            .zIndex(1f)  // Đảm bảo header luôn ở trên cùng
    )  {
        Column {
            // Search Bar and Profile/Cart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search field
                SearchBar(
                    placeholder = "Search for \"Grocery\"",
                    onClick = onNavigateToSearch,
                    modifier = Modifier.weight(1f)
                )

                // Profile button
                CircularIconButton(
                    icon = if (isUserLoggedIn) Icons.Default.Person else Icons.Default.Login,
                    contentDescription = if (isUserLoggedIn) "Profile" else "Login",
                    onClick = onProfileClick,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
                {
                    /// Cart button trong HeaderSection
                    CartButton(
                        itemCount = cartItemCount,
                        onClick   = onCartClick,
                        modifier  = Modifier.padding(start = 8.dp)
                    )
                }
            }

            // Location info
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Current Location",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = locationName,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}