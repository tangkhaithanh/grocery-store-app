package com.store.grocery_store_app.ui.screens.ProductDetails.components
/**
 * Price information section with discount display
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.store.grocery_store_app.ui.theme.DeepTeal
import com.store.grocery_store_app.ui.theme.Gray600
import java.math.BigDecimal
import java.text.NumberFormat

@Composable
fun PriceSection(
    price: BigDecimal,
    effectivePrice: BigDecimal,
    currencyFormatter: NumberFormat
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (effectivePrice < price) {
            Text(
                text = currencyFormatter.format(price),
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = TextDecoration.LineThrough,
                color = Gray600
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = currencyFormatter.format(effectivePrice),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = DeepTeal
        )

        // Show discount percentage if there is a discount
        if (effectivePrice < price) {
            val discountPercentage = ((1 - (effectivePrice.toDouble() / price.toDouble())) * 100).toInt()
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.error)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
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
}