package com.store.grocery_store_app.ui.screens.ProductDetails.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetValue.Hidden
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.ui.components.ErrorDialog
import com.store.grocery_store_app.ui.components.LoadingDialog
import com.store.grocery_store_app.ui.components.SuccessDialog
import com.store.grocery_store_app.ui.screens.cart.CartViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddToCartSheet(
    product : ProductResponse,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit = {},
    onNavigateToCart: () -> Unit = {},
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN")).apply {
        maximumFractionDigits = 0
    }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()  // Tạo scope cho coroutine
    val cartState by cartViewModel.state.collectAsState()
    var quantityCart by remember { mutableStateOf(1) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        shape = RoundedCornerShape(
            topStart = 16.dp,  // Bo góc trên bên trái
            topEnd = 16.dp,    // Bo góc trên bên phải
            bottomStart = 0.dp, // Không bo góc dưới bên trái
            bottomEnd = 0.dp    // Không bo góc dưới bên phải
        ),
    ) {
        LoadingDialog(
            isLoading = cartState.isLoading,
            message = "Đang thêm sản phẩm vào giỏ hàng"
        )
//        if(cartState.isSuccess) {
//            SuccessDialog(
//                title = "Thêm thành công",
//                content = "Sản phẩm đã được thêm vào giỏ hàng",
//                clearError = cartViewModel::clearError,
//                confirmButtonRequest = onNavigateToCart
//            )
//        }
        if(cartState.error!=null) {
            ErrorDialog(
                title = "Thêm thất bại",
                content = "Thêm sản phẩm vô giỏ hàng thất bại",
                clearError = cartViewModel::clearError
            )
        }


        Box(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                )
                .fillMaxWidth()
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        bottom = 64.dp
                    )
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = product.imageUrls[0],
                        contentDescription = "Product Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)) // Bo góc
                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    Column (
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = currencyFormatter.format(product.effectivePrice),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Red
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Kho: ${product.quantity-product.soldCount}")
                    }

                }
                // Đường cắt ngang
                Divider(
                    color = Color.LightGray,
                    thickness = 2.dp,

                )

                Row (
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Số lượng", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(4.dp))
                            .padding(2.dp)

                    ) {

                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Subtract quantity",
                            modifier = Modifier
                                .clickable {
                                    if (quantityCart > 1) quantityCart--
                                }
                                .size(16.dp)
                                .alpha(0.5f)

                        )

                        // Bọc Text vào Box và thêm border trái/phải
                        Box(
                            modifier = Modifier
                                .drawBehind {
                                    val strokeWidth = 1.dp.toPx()
                                    val color = Color.LightGray
                                    // Vẽ viền trái
                                    drawLine(
                                        color = color,
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                    // Vẽ viền phải
                                    drawLine(
                                        color = color,
                                        start = Offset(size.width, 0f),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = strokeWidth
                                    )
                                }
                                .padding(horizontal = 10.dp)
                                .widthIn(max = 26.dp) // Ensure the TextField doesn't overflow

                        ) {
                            BasicTextField(
                                value = quantityCart.toString(),
                                onValueChange = { input ->
                                    val maxQuantity = product.quantity - product.soldCount

                                    when {
                                        input.isEmpty() -> {
                                            quantityCart = 1
                                        }
                                        input.all { it.isDigit() } -> {
                                            val inputNumber = input.toIntOrNull() ?: 1
                                            quantityCart = if (inputNumber > maxQuantity) maxQuantity else inputNumber
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .height(16.dp)
                                    .padding(horizontal = 4.dp),
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = Color.Red
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Plus quantity",
                            modifier = Modifier
                                .clickable {
                                    if (quantityCart < product.quantity-product.soldCount) quantityCart++
                                }
                                .size(16.dp)
                                .alpha(0.5f)

                        )

                    }
                }
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Đóng sheet",
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        scope.launch {
                            bottomSheetState.hide()
                            onDismiss()
                        }
                    },
                tint = Color.Black
            )
            AddToCartButton(
                remaining = product.quantity - product.soldCount,
                onAddToCart = {
                    var img : String? = null
                    if(product.imageUrls.isNotEmpty()) {
                        img = product.imageUrls.get(0)
                    }
                    cartViewModel.insertProductIntoCart(
                        idCart = null,
                        flashSaleId = null,
                        quantity = quantityCart,
                        priceCart = product.effectivePrice,
                        idProduct = product.id,
                        name = product.name,
                        priceProduct = product.price,
                        imageUrl = img
                    )

                },
                // ⬇⬇ Lấy đúng tọa độ tâm nút
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)

                // hoặc kích thước bạn muốn

            )
        }
    }
}