package com.store.grocery_store_app.ui.screens.order


import android.util.Log
import androidx.compose.animation.* // Kept for when we re-enable
import androidx.compose.animation.core.* // Kept for when we re-enable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.store.grocery_store_app.data.models.DeliveryDetail
import com.store.grocery_store_app.data.models.StatusOrderType
import kotlinx.coroutines.delay // Kept for when we re-enable

// Enum cho trạng thái của từng bước
enum class StepStatusForStaticPreview {
    COMPLETED,
    CURRENT, // Will be used for the first item if it's "Waiting for Confirmation"
    PENDING  // Or PENDING, depending on visual preference for the first item
}

// Định nghĩa màu trực tiếp
private val colorDeepTeal = Color(0xFF004D40)
private val colorLightGrey = Color(0xFFD3D3D3) // For pending timeline line
private val colorBackgroundGreyScreen = Color(0xFFF5F5F5) // Main screen background
private val colorOrangeCTA = Color(0xFFFFA726)
private val colorWhite = Color(0xFFFFFFFF)
private val colorDarkText = Color(0xFF333333)
private val colorLightText = Color(0xFF6C757D) // A slightly darker grey for subtitles, as in image

// Item Background Colors from the original image
private val colorItemBgWaiting = Color(0xFFEEEEEE)
private val colorItemBgDeliveringHome = Color(0xFFE7F3E8)
private val colorItemBgPackaging = Color(0xFFE4EFFB)
private val colorItemBgOrderConfirmed = Color(0xFFFFF3E1)
private val colorItemBgOrderReceived = Color(0xFFFCE5E7)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDetailScreen(
    deliveryOrder : String,
    onBackClicked: () -> Unit = {},
    deliveryDetailViewModel: DeliveryDetailViewModel = hiltViewModel()
) {
    val state = deliveryDetailViewModel.state.collectAsState()
    val order = state.value.order
    var messageStep3 : String = ""

    LaunchedEffect(Unit) {
        deliveryDetailViewModel.getOrder(deliveryOrder)
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delivery Details", color = colorWhite) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = colorWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorDeepTeal)
            )
        },
        containerColor = colorBackgroundGreyScreen
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize() // Ensures the Column takes all available space
                .background(colorBackgroundGreyScreen)
                .verticalScroll(rememberScrollState())
        ) {
            // Header from original image
            DeliveryHeader_StaticOriginal(
                date = "March 5, 2019",
                time = "6:30 pm"
            )

            Column(
                modifier = Modifier
                    .weight(1f) // Takes remaining space
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                // Item 1: Waiting of Confirmation
                DeliveryStepItem_StaticOriginal(
                    itemKey = "static_step1",
                    title = "Cửa hàng đã xác nhận đơn hàng",
                    dateTimeInfo = "Ngày 13 tháng 05 năm 2025, 09:26 AM",
                    status = StepStatusForStaticPreview.COMPLETED, // As per empty circle in image
                    previousStepStatus = null,
                    isFirst = true,
                    isLast = false,
                    indexInList = 0, // For potential animation staggering
                    cardBackgroundColor = colorItemBgDeliveringHome
                )

                // Item 2: Delivering to Home
                DeliveryStepItem_StaticOriginal(
                    itemKey = "static_step2",
                    title = "Shipper đã lấy hàng",
                    dateTimeInfo = "Ngày 13 tháng 05 năm 2025, 10:26 AM",
                    status = StepStatusForStaticPreview.COMPLETED, // Checkmark in image
                    previousStepStatus = StepStatusForStaticPreview.PENDING, // Status of item 1
                    isFirst = false,
                    isLast = false,
                    indexInList = 1,
                    cardBackgroundColor = colorItemBgDeliveringHome
                )
                if (order != null) {
                    if(order.status == StatusOrderType.SHIPPED) {
                        // Item 3:
                        DeliveryStepItem_StaticOriginal(
                            itemKey = "static_step3",
                            title = "Đang giao hàng",
                            dateTimeInfo = ".....",
                            status = StepStatusForStaticPreview.COMPLETED, // Checkmark in image
                            previousStepStatus = StepStatusForStaticPreview.COMPLETED, // Status of item 2
                            isFirst = false,
                            isLast = false,
                            indexInList = 2,
                            cardBackgroundColor = colorItemBgPackaging
                        )
                    }
                    else if(order.status == StatusOrderType.DELIVERED) {
                        DeliveryStepItem_StaticOriginal(
                            itemKey = "static_step3",
                            title = "Đã giao hàng thành công",
                            dateTimeInfo = order.deliveredAt,
                            status = StepStatusForStaticPreview.COMPLETED, // Checkmark in image
                            previousStepStatus = StepStatusForStaticPreview.COMPLETED, // Status of item 2
                            isFirst = false,
                            isLast = false,
                            indexInList = 2,
                            cardBackgroundColor = colorItemBgPackaging
                        )
                    }
                }

                if(order!=null) {
                    if(order.status == StatusOrderType.COMPLETED) {
                        // Item 4: Your order is confirmed
                        DeliveryStepItem_StaticOriginal(
                            itemKey = "static_step4",
                            title = "Đã nhận được hàng",
                            dateTimeInfo = "Ngày 14 tháng 5 năm 2025, 11:26 AM",
                            status = StepStatusForStaticPreview.COMPLETED, // Checkmark in image
                            previousStepStatus = StepStatusForStaticPreview.COMPLETED, // Status of item 3
                            isFirst = false,
                            isLast = false,
                            indexInList = 3,
                            cardBackgroundColor = colorItemBgOrderConfirmed
                        )
                    }
                }

            }

            // "Contact with Support" button remains
            ContactSupportButton_StaticOriginal(
                modifier = Modifier.padding(16.dp) // Consistent padding
            )
        }
    }
}

@Composable
private fun DeliveryHeader_StaticOriginal(date: String, time: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text("Delivered on", fontSize = 14.sp, color = colorLightText)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.CalendarToday, "Delivery Date", tint = colorDarkText, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(date, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = colorDarkText)
            Spacer(modifier = Modifier.weight(1f))
            Text(time, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = colorDeepTeal)
        }
    }
}

@Composable
private fun DeliveryStepItem_StaticOriginal(
    itemKey: String,
    title: String,
    dateTimeInfo: String,
    status: StepStatusForStaticPreview,
    previousStepStatus: StepStatusForStaticPreview?,
    isFirst: Boolean,
    isLast: Boolean,
    indexInList: Int, // Kept for potential re-introduction of staggered animation
    cardBackgroundColor: Color
) {
    val iconSize = 24.dp
    val lineWidth = 2.dp

    // --- DIAGNOSTIC: Animation Disabled ---
    // var itemVisible by remember(itemKey) { mutableStateOf(false) }
    // LaunchedEffect(key1 = itemKey) {
    //     delay(indexInList * 100L)
    //     itemVisible = true
    // }
    // AnimatedVisibility(
    //     visible = itemVisible, // For now, let's try without animation to see if items render
    //     enter = fadeIn(animationSpec = tween(400, easing = LinearOutSlowInEasing)) +
    //             slideInVertically(initialOffsetY = { it / 2 }, animationSpec = tween(400, easing = LinearOutSlowInEasing)),
    //     exit = fadeOut(animationSpec = tween(200)),
    //     key = itemKey
    // ) {
    // --- END DIAGNOSTIC ---

    // Direct rendering without AnimatedVisibility for testing
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 0.dp) // Ensure items are close for timeline connection
            .height(IntrinsicSize.Min) // Important for TimelineIndicator's fillMaxHeight
    ) {
        TimelineIndicator_StaticOriginal(status, previousStepStatus, isFirst, isLast, iconSize, lineWidth)
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = cardBackgroundColor), // Use passed background color
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp), // Subtle shadow
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp) // Padding for the card itself
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = colorDarkText)
                Spacer(modifier = Modifier.height(4.dp))
                Text(dateTimeInfo, fontSize = 13.sp, color = colorLightText)
            }
        }
    }
    // } // Closing brace for AnimatedVisibility if re-enabled
}

@Composable
private fun TimelineIndicator_StaticOriginal(
    status: StepStatusForStaticPreview,
    previousStepStatus: StepStatusForStaticPreview?,
    isFirst: Boolean,
    isLast: Boolean,
    iconSize: Dp,
    lineWidth: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxHeight() // Fills the height of the parent Row
            .width(iconSize) // Width of the icon itself
    ) {
        val topColor = when (previousStepStatus) {
            StepStatusForStaticPreview.COMPLETED, StepStatusForStaticPreview.CURRENT -> colorDeepTeal
            StepStatusForStaticPreview.PENDING -> colorDeepTeal // Line from pending should also be colored if next is complete
            null -> Color.Transparent // No line above the first item
            else -> colorLightGrey // Default for safety
        }
        Box(
            Modifier
                .weight(1f) // Takes space above icon
                .width(lineWidth)
                .background(if (isFirst) Color.Transparent else topColor)
        )

        StatusIcon_StaticOriginal(status = status, iconSize = iconSize, lineWidth = lineWidth)

        // Bottom line color depends on the current item's status for connection to next
        val bottomColor = when (status) {
            StepStatusForStaticPreview.COMPLETED, StepStatusForStaticPreview.CURRENT, StepStatusForStaticPreview.PENDING -> colorDeepTeal
            // If current is PENDING but it's not the last, the line to next still exists
            else -> colorLightGrey
        }
        Box(
            Modifier
                .weight(1f) // Takes space below icon
                .width(lineWidth)
                .background(if (isLast) Color.Transparent else bottomColor)
        )
    }
}

@Composable
private fun StatusIcon_StaticOriginal(status: StepStatusForStaticPreview, iconSize: Dp, lineWidth: Dp) {
    Box(modifier = Modifier.size(iconSize), contentAlignment = Alignment.Center) {
        when (status) {
            StepStatusForStaticPreview.COMPLETED -> {
                // --- DIAGNOSTIC: Animation Disabled ---
                // var scale by remember { mutableStateOf(0.5f) }
                // LaunchedEffect(key1 = Unit) {
                //     animate(initialValue = 0.5f, targetValue = 1f, animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) { value, _ -> scale = value }
                // }
                Icon(Icons.Filled.CheckCircle, "Completed", tint = colorDeepTeal, modifier = Modifier.fillMaxSize()/*.scale(scale)*/)
            }
            StepStatusForStaticPreview.CURRENT -> { // For an active, pulsing dot if needed
                val infiniteTransition = rememberInfiniteTransition("current_step_pulse_static")
                val pulseScale by infiniteTransition.animateFloat(1f, 1.3f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse), "pulse_scale_static")
                val pulseAlpha by infiniteTransition.animateFloat(0.7f, 0.3f, infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse), "pulse_alpha_static")
                Box(modifier = Modifier.size(iconSize * pulseScale).clip(CircleShape).background(colorDeepTeal.copy(alpha = pulseAlpha)))
                Box(modifier = Modifier.size(iconSize * 0.7f).clip(CircleShape).background(colorDeepTeal))
            }
            StepStatusForStaticPreview.PENDING -> { // Empty outlined circle
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(colorLightGrey, radius = (iconSize / 2).toPx() - (lineWidth / 2).toPx(), style = Stroke(width = lineWidth.toPx()))
                }
            }
        }
    }
}

@Composable
private fun ContactSupportButton_StaticOriginal(modifier: Modifier = Modifier) {
    Button(
        onClick = { /* No action needed for this preview */ },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorDeepTeal)
    ) {
        Icon(Icons.AutoMirrored.Filled.Chat, "Chat Icon", tint = colorWhite)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Liên hệ với cửa hàng", color = colorWhite, fontWeight = FontWeight.Medium)
    }
}

