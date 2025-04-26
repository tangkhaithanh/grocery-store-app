package com.store.grocery_store_app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.ui.components.CustomButton
import com.store.grocery_store_app.ui.screens.auth.AuthViewModel
import com.store.grocery_store_app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val scrollState = rememberScrollState()

    // Check if the user is logged in
    val isUserLoggedIn = authState.isLoggedIn

    var showProfileMenu by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigation()
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundWhite)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header with search and location
                HeaderSection(
                    isUserLoggedIn = isUserLoggedIn,
                    onProfileClick = { showProfileMenu = true }
                )

                // Categories
                CategoriesSection()

                // Recommendation section
                RecommendationSection()

                // Delivery options
                DeliveryOptionsSection()

                // Featured section header (just the title)
                FeaturedSectionHeader()

                // Bottom space for scrolling
                Spacer(modifier = Modifier.height(50.dp))
            }

            // Profile menu dropdown
            if (showProfileMenu) {
                ProfileMenu(
                    isLoggedIn = isUserLoggedIn,
                    userName = authState.userName ?: "Người dùng",
                    userEmail = authState.userEmail ?: "",
                    onDismiss = { showProfileMenu = false },
                    onLogoutClick = {
                        authViewModel.logout()
                        showProfileMenu = false
                        onLogout() // Sử dụng onLogout callback từ NavGraph
                    }
                )
            }
        }
    }
}

@Composable
fun HeaderSection(
    isUserLoggedIn: Boolean,
    onProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepTeal)
    ) {
        Column {
            // Search Bar and Profile/Cart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search field
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Gray500,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Search for \"Grocery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Gray500,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Profile button
                Card(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp)
                        .clickable { onProfileClick() },
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
                            imageVector = if (isUserLoggedIn) Icons.Default.Person else Icons.Default.Login,
                            contentDescription = if (isUserLoggedIn) "Profile" else "Login",
                            tint = DeepTeal
                        )
                    }
                }

                // Cart button
                Card(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .size(40.dp),
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
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart",
                            tint = DeepTeal
                        )
                    }
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
                        text = "California, USA",
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

@Composable
fun ProfileMenu(
    isLoggedIn: Boolean,
    userName: String,
    userEmail: String,
    onDismiss: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        Card(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 80.dp, end = 16.dp)
                .width(280.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (isLoggedIn) {
                    // User info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User",
                            tint = DeepTeal,
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 12.dp)
                        )

                        Column {
                            Text(
                                text = userName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = userEmail,
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600
                            )
                        }
                    }

                    Divider()

                    // Menu options
                    MenuOption(
                        icon = Icons.Default.Settings,
                        title = "Cài đặt tài khoản"
                    )

                    MenuOption(
                        icon = Icons.Default.History,
                        title = "Lịch sử đơn hàng"
                    )

                    MenuOption(
                        icon = Icons.Default.Logout,
                        title = "Đăng xuất",
                        onClick = onLogoutClick
                    )
                } else {
                    // Nếu không đăng nhập, hiển thị thông báo và nút đăng xuất
                    // để chuyển về màn hình login
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Info",
                            tint = DeepTeal,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "Bạn chưa đăng nhập",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                        )

                        Button(
                            onClick = onLogoutClick, // Sử dụng onLogout để chuyển về trang Login
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepTeal
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = "Login",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Đăng nhập / Đăng ký")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MenuOption(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = DeepTeal,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun CategoriesSection() {
    val categories = listOf(
        Triple("Breads", Icons.Default.BakeryDining, IconBread),
        Triple("Cleaners", Icons.Default.CleaningServices, IconCleaner),
        Triple("Sweets", Icons.Default.Cake, IconSweets),
        Triple("Dairy", Icons.Default.Icecream, IconDairy)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundWhite)
    ) {
        // White curved background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(
                    color = DeepTeal,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
        )

        // Categories row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(categories) { (name, icon, bgColor) ->
                CategoryItem(name = name, icon = icon, bgColor = bgColor)
            }
        }
    }
}

@Composable
fun CategoryItem(
    name: String,
    icon: ImageVector,
    bgColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon with background
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = name,
                tint = DeepTeal,
                modifier = Modifier.size(28.dp)
            )
        }

        // Label
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Selected indicator (only for "Cleaners")
        if (name == "Cleaners") {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(24.dp)
                    .height(2.dp)
                    .background(
                        color = Color.Red,
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }
    }
}

@Composable
fun RecommendationSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "You might need",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = DeepTeal
                )
            )

            Text(
                text = "See more",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFFE57373)
            )
        }

        // Products row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProductCard(
                name = "Beetroot\n(Local shop)",
                price = "17.29",
                quantity = "500 gm.",
                tint = ProductRed,
                modifier = Modifier.weight(1f)
            )

            ProductCard(
                name = "Italian Avocado\n(Local shop)",
                price = "14.29",
                quantity = "450 gm.",
                tint = ProductGreen,
                modifier = Modifier.weight(1f)
            )

            ProductCard(
                name = "Deshi Gajor\n(Local Carrot)",
                price = "27.29",
                quantity = "1000 gm.",
                tint = ProductOrange,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProductCard(
    name: String,
    price: String,
    quantity: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Product image (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(tint.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = null,
                        tint = tint,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            // Product name
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Quantity
            Text(
                text = quantity,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )

            // Price and Add button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$price৳",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(
                            width = 1.dp,
                            color = Gray300,
                            shape = RoundedCornerShape(4.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add",
                        tint = DeepTeal,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryOptionsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Grocery option
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardGrocery
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Grocery",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "By 12:15pm",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray700
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Free delivery",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // Wholesale option
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardWholesale
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Wholesale",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Text(
                    text = "By 1:30pm",
                    style = MaterialTheme.typography.bodySmall,
                    color = Gray700
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Free delivery",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun FeaturedSectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Featured",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = DeepTeal
            )
        )

        Text(
            text = "See all",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFFE57373)
        )
    }
}

@Composable
fun BottomNavigation() {
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Catalog" to Icons.Default.ViewList,
        "Favorites" to Icons.Default.FavoriteBorder,
        "Delivery" to Icons.Default.LocalShipping
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, (text, icon) ->
                val selected = index == 0 // Home is selected

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(4.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        tint = if (selected) DeepTeal else Gray500,
                        modifier = Modifier.size(24.dp)
                    )

                    if (selected) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall,
                            color = DeepTeal
                        )
                    }
                }
            }
        }
    }
}