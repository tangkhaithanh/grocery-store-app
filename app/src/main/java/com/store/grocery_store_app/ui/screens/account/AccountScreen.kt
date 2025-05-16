package com.store.grocery_store_app.ui.screens.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.screens.home.components.BottomNavigation
import com.store.grocery_store_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onNavigateToHome: () -> Unit = {},
    onNavigateToCategory: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToOrder: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddress: () -> Unit = {},
    onNavigateToFavoriteProducts: () -> Unit = {},
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // State cho việc hiển thị dialog avatar
    var showAvatarDialog by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                onFabClick = onNavigateToOrder,
                notificationCount = 10,
                onNavigateToHome = onNavigateToHome,
                onNavigateToCategory = onNavigateToCategory,
                onNavigateToNotification = onNavigateToNotification,
                onNavigateToAccount = { /* Đã ở trang Account */ },
                selectedTab = 3  // Tab Account (index = 3)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // Compact Top Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Smaller Profile Image với click để hiển thị dialog
                    Card(
                        modifier = Modifier
                            .size(80.dp)
                            .clickable { showAvatarDialog = true },
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(authState.userImage ?: "")
                                .crossfade(true)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Smaller User Name
                    Text(
                        text = authState.userName ?: "Người dùng",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // Smaller User Email
                    Text(
                        text = authState.userEmail ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Menu Items - Tương tự như cũ nhưng với spacing nhỏ hơn
            val menuItems = listOf(
                AccountMenuItem(
                    icon = Icons.Default.LocationOn,
                    title = "Sổ địa chỉ",
                    subtitle = "Quản lý thông tin giao hàng",
                    onClick = onNavigateToAddress
                ),
                AccountMenuItem(
                    icon = Icons.Default.Favorite,
                    title = "Sản phẩm yêu thích",
                    subtitle = "Xem các sản phẩm đã lưu",
                    onClick = onNavigateToFavoriteProducts
                ),
                AccountMenuItem(
                    icon = Icons.Default.Star,
                    title = "Các đánh giá",
                    subtitle = "Quản lý đánh giá của bạn",
                    onClick = { /* TODO: Navigate to Reviews */ }
                ),
                AccountMenuItem(
                    icon = Icons.Default.Edit,
                    title = "Chỉnh sửa hồ sơ",
                    subtitle = "Cập nhật thông tin cá nhân",
                    onClick = { onNavigateToProfile() }
                ),
                AccountMenuItem(
                    icon = Icons.Default.Settings,
                    title = "Cài đặt",
                    subtitle = "Tùy chọn ứng dụng",
                    onClick = { /* TODO: Navigate to Settings */ }
                ),
                AccountMenuItem(
                    icon = Icons.Default.ExitToApp,
                    title = "Đăng xuất",
                    subtitle = "Thoát khỏi tài khoản",
                    onClick = { authViewModel.logout() }
                )
            )

            // Render menu items with tighter spacing
            menuItems.forEach { item ->
                AccountMenuItemCard(
                    item = item,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    // Avatar Dialog
    if (showAvatarDialog) {
        AvatarViewDialog(
            imageUrl = authState.userImage,
            onDismiss = { showAvatarDialog = false }
        )
    }
}

@Composable
fun AccountMenuItemCard(
    item: AccountMenuItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { item.onClick() },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.width(14.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Arrow Icon
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

data class AccountMenuItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit
)

@Composable
fun AvatarViewDialog(
    imageUrl: String?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f)),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Close button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .background(
                                    Color.White.copy(alpha = 0.2f),
                                    CircleShape
                                )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White
                            )
                        }
                    }

                    // Large avatar image
                    Card(
                        modifier = Modifier
                            .size(300.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* Prevent closing when clicking on image */ },
                        shape = CircleShape,
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .placeholder(android.R.drawable.ic_menu_gallery)
                                .error(android.R.drawable.ic_menu_gallery)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // User name below image
                    Text(
                        text = "Ảnh đại diện",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}