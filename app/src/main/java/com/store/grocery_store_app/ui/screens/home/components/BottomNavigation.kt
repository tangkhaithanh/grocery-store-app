package com.store.grocery_store_app.ui.screens.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.store.grocery_store_app.R

@Composable
fun BottomNavigation(
    onFabClick: () -> Unit = {},
    notificationCount: Int = 0
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val items = listOf(
        NavigationItem("Home", Icons.Default.Home),
        NavigationItem("Categories", Icons.Default.Category),
        NavigationItem("Notification", Icons.Default.Notifications, badgeCount = notificationCount),
        NavigationItem("Account", Icons.Default.AccountCircle)
    )

    Box {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.take(2).forEachIndexed { index, item ->
                    BottomNavItem(
                        item = item,
                        selected = (selectedIndex == index),
                        onClick = { selectedIndex = index }
                    )
                }

                Spacer(modifier = Modifier.width(64.dp)) // chỗ cho FAB

                items.drop(2).forEachIndexed { offset, item ->
                    val index = offset + 2
                    BottomNavItem(
                        item = item,
                        selected = (selectedIndex == index),
                        onClick = { selectedIndex = index }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onFabClick,
            containerColor = MaterialTheme.colorScheme.primary,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-32).dp)
                .size(64.dp),
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_package),
                contentDescription = "Order",
                tint = Color.White)
        }
    }
}

data class NavigationItem(
    val text: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavItem(
    item: NavigationItem,
    selected: Boolean,
    onClick: () -> Unit
) {
    // Animation cho trạng thái chọn
    val transition = updateTransition(targetState = selected, label = "ItemSelectedTransition")
    val iconSize by transition.animateDp(label = "IconSize") { if (it) 28.dp else 24.dp }
    val iconOffsetY by transition.animateDp(label = "IconOffsetY") { if (it) (-2).dp else 0.dp }
    val backgroundColor by transition.animateColor(label = "BackgroundColor") { if (it) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else Color.Transparent }
    val iconColor by transition.animateColor(label = "IconColor") { if (it) MaterialTheme.colorScheme.primary else Color.Gray }
    val textColor by transition.animateColor(label = "TextColor") { if (it) MaterialTheme.colorScheme.primary else Color.Gray.copy(alpha = 0.7f) }

    // Animation press hiệu ứng scale
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(pressScale)
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true, radius = 24.dp),
                onClick = onClick
            )
            .padding(vertical = 8.dp, horizontal = 8.dp)
    ) {
        BadgedBox(
            badge = {
                if (item.badgeCount > 0) {
                    Badge { Text(item.badgeCount.toString()) }
                }
            },
            modifier = Modifier
                .offset(y = iconOffsetY)
                .size(35.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize() // full size of BadgedBox, so badge không bị cắt
                    .clip(CircleShape)
                    .background(backgroundColor)
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.text,
                    tint = iconColor,
                    modifier = Modifier.size(iconSize)
                )
            }
        }


        AnimatedVisibility(
            visible = selected,
            enter = fadeIn(tween(300)) + expandVertically(),
            exit = fadeOut(tween(300)) + shrinkVertically()
        ) {
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = textColor,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BottomNavigationPreview() {
    BottomNavigation(notificationCount = 10)
}
