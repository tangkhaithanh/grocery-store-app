package com.store.grocery_store_app.ui.screens.home.components
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray600
import java.text.NumberFormat
import java.util.*
/**
 * Reusable product card component.
 *
 * @param image URL of the product image
 * @param name Product name
 * @param category Category name of the product
 * @param categoryColor Color associated with the category
 * @param price Original price of the product
 * @param effectivePrice Current price after any discounts
 * @param rating Rating of the product (null if not rated)
 * @param soldCount Number of units sold
 * @param quantity Total quantity of the product
 * @param badgeText Optional text to show in the top-left badge
 * @param badgeBackgroundColor Background color for the badge
 * @param discountPercentage Discount percentage to show (null for no discount badge)
 * @param addToCartButtonColor Color for the add to cart button
 * @param priceColor Color for displaying the price
 * @param showAddToCartButton Whether to show the add to cart button
 * @param showQuantity Whether to show remaining quantity
 * @param showRating Whether to show the rating
 * @param addToCartIcon Icon to display in the add to cart button
 * @param currencyLocale Locale for formatting currency
 * @param onProductClick Callback when the product card is clicked
 * @param onAddToCartClick Callback when the add to cart button is clicked
 */
@Composable
fun CustomProductCard(
    image: String? = null,
    name: String,
    category: String,
    categoryColor: Color,
    price: Double,
    effectivePrice: Double = price,
    rating: Double? = null,
    soldCount: Int = 0,
    quantity: Int = 0,
    badgeText: String? = null,
    badgeBackgroundColor: Color = Color(0xFFFF5252),
    discountPercentage: Int? = null,
    addToCartButtonColor: Color = DeepTeal,
    priceColor: Color = DeepTeal,
    showAddToCartButton: Boolean = true,
    showQuantity: Boolean = true,
    showRating: Boolean = true,
    addToCartIcon: ImageVector = Icons.Default.Add,
    currencyLocale: Locale = Locale("vi", "VN"),
    onProductClick: () -> Unit,
    onAddToCartClick: () -> Unit = {}
) {
    val priceFormatter = NumberFormat.getCurrencyInstance(currencyLocale)
    priceFormatter.maximumFractionDigits = 0

    Card(
        modifier = Modifier
            .width(180.dp)
            .clickable(onClick = onProductClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Product image with badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                // Main product image (rectangular)
                if (!image.isNullOrEmpty()) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image)
                            .crossfade(true)
                            .build(),
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .shimmerEffect()
                                )
                            }
                            is AsyncImagePainter.State.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(categoryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBasket,
                                        contentDescription = null,
                                        tint = categoryColor,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                            is AsyncImagePainter.State.Success -> {
                                SubcomposeAsyncImageContent()
                            }
                            else -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(categoryColor.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBasket,
                                        contentDescription = null,
                                        tint = categoryColor,
                                        modifier = Modifier.size(48.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(categoryColor.copy(alpha = 0.1f))
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBasket,
                            contentDescription = null,
                            tint = categoryColor,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Custom badge (Best seller, etc)
                if (!badgeText.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(badgeBackgroundColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopStart)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Discount badge
                if (discountPercentage != null && discountPercentage > 0) {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.error)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "-$discountPercentage%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Product details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Category name
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Product name
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                // Rating and sold count
                if (showRating || soldCount > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        // Rating
                        if (showRating && rating != null && rating > 0) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = String.format("%.1f", rating),
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600,
                                modifier = Modifier.padding(start = 2.dp, end = 8.dp)
                            )
                        }

                        // Sold count
                        if (soldCount > 0) {
                            Text(
                                text = "Đã bán $soldCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = Gray600
                            )
                        }
                    }
                }

                // Quantity
                if (showQuantity && quantity > 0) {
                    val remaining = quantity - soldCount

                    Text(
                        text = "Còn lại $remaining sản phẩm",
                        style = MaterialTheme.typography.bodySmall,
                        color = Gray600,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Price and add to cart
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Price section
                    Column {
                        // Original price with strikethrough if discounted
                        if (effectivePrice < price) {
                            Text(
                                text = priceFormatter.format(price),
                                style = MaterialTheme.typography.bodySmall.copy(
                                    textDecoration = TextDecoration.LineThrough
                                ),
                                color = Gray600
                            )
                        }

                        // Current effective price
                        Text(
                            text = priceFormatter.format(effectivePrice),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = priceColor
                        )
                    }

                    // Add to cart button
                    if (showAddToCartButton) {
                        Button(
                            onClick = onAddToCartClick,
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = addToCartButtonColor,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Icon(
                                imageVector = addToCartIcon,
                                contentDescription = "Add to cart",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}