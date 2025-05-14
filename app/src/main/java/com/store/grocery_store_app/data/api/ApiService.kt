package com.store.grocery_store_app.data.api

import com.store.grocery_store_app.data.models.StatusOrderType
import com.store.grocery_store_app.data.models.request.AuthRequest
import com.store.grocery_store_app.data.models.request.CartItemRequest
import com.store.grocery_store_app.data.models.request.ForgotPasswordRequest
import com.store.grocery_store_app.data.models.request.OtpRequest
import com.store.grocery_store_app.data.models.request.OtpVerifyRequest
import com.store.grocery_store_app.data.models.request.RegisterRequest
import com.store.grocery_store_app.data.models.request.ReviewRequest
import com.store.grocery_store_app.data.models.request.UpdateUserRequest
import com.store.grocery_store_app.data.models.response.ApiResponse
import com.store.grocery_store_app.data.models.response.AuthResponse
import com.store.grocery_store_app.data.models.response.CartResponse
import com.store.grocery_store_app.data.models.response.CategoryResponse
import com.store.grocery_store_app.data.models.response.FlashSaleResponse
import com.store.grocery_store_app.data.models.response.OrderItemResponse
import com.store.grocery_store_app.data.models.response.OrderResponse
import com.store.grocery_store_app.data.models.response.PagedResponse
import com.store.grocery_store_app.data.models.response.ProductResponse
import com.store.grocery_store_app.data.models.response.ReviewResponse
import com.store.grocery_store_app.data.models.response.ReviewStatsResponse
import com.store.grocery_store_app.data.models.response.UserDTO
import com.store.grocery_store_app.data.models.response.VoucherResponse
import com.store.grocery_store_app.data.models.request.*
import com.store.grocery_store_app.data.models.response.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<Any>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Any>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Query("refreshToken") refreshToken: String): Response<ApiResponse<String>>

    @POST("otp/send")
    suspend fun sendOtp(@Body request: OtpRequest): Response<ApiResponse<Any>>

    @POST("otp/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<ApiResponse<Any>>

    @GET("categories")
    suspend fun getCategories(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 100
    ): Response<ApiResponse<PagedResponse<CategoryResponse>>>

    @GET("product/best-seller")
    suspend fun getBestSellerProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ProductResponse>>>

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("typeStatusOrder") typeStatusOrder: StatusOrderType = StatusOrderType.ALL
    ): Response<ApiResponse<PagedResponse<OrderResponse>>>

    @GET("favourites/get")
    suspend fun getFavouriteProducts(): Response<ApiResponse<List<ProductResponse>>>

    @POST("favourites/add")
    suspend fun addToFavourite(@Query("productId") productId: Long): Response<ApiResponse<Any>>

    @DELETE("favourites/delete")
    suspend fun removeFromFavourite(@Query("productId") productId: Long): Response<ApiResponse<Any>>

    @GET("product")
    suspend fun getAllProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ProductResponse>>>

    @GET("product/details/{id}")
    suspend fun getProductDetail(@Path("id") id: Long): Response<ApiResponse<ProductResponse>>

    @GET("reviews/product/{productId}")
    suspend fun getProductReviews(
        @Path("productId") productId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ReviewResponse>>>

    @GET("reviews/stats/{productId}")
    suspend fun getProductReviewStats(
        @Path("productId") productId: Long
    ): Response<ApiResponse<ReviewStatsResponse>>

    @GET("product/by-category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: Long,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ProductResponse>>>

    @GET("orderItems")
    suspend fun getOrderItemById(
        @Query("orderItemId") orderItemId: Long
    ): Response<ApiResponse<OrderItemResponse>>

    @GET("product/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<ApiResponse<PagedResponse<ProductResponse>>>

    @POST("reviews/add")
    suspend fun createReview(
        @Body reviewRequest: ReviewRequest,
        @Query("orderItemId") orderItemId: Long
    ): Response<ApiResponse<Any>>

    @POST("carts/addToCart")
    suspend fun insertProductIntoCart(
        @Body cartItemRequest: CartItemRequest
    ): Response<ApiResponse<Any>>

    @POST("carts/updateToCart")
    suspend fun updateProductIntoCart(
        @Body cartItemRequest: CartItemRequest
    ): Response<ApiResponse<Any>>

    @GET("carts")
    suspend fun getAllCartItem(): Response<ApiResponse<CartResponse>>

    @GET("vouchers")
    suspend fun getAllVoucher(): Response<ApiResponse<List<VoucherResponse>>>

    // API for user:
    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: Long): Response<ApiResponse<UserDTO>>

    @PUT("user/update/{id}")
    suspend fun updateUser(@Path("id") id: Long, @Body request: UpdateUserRequest): Response<ApiResponse<UserDTO>>

    @DELETE("carts/items/{cartItemId}")
    suspend fun removeCartItem(@Path("cartItemId") id: Long): Response<ApiResponse<Any>>

    @GET("orders/order")
    suspend fun getOrder(
        @Query("orderId") orderId: Long
    ) : Response<ApiResponse<OrderResponse>>

    @GET("flash-sale")
    suspend fun getFlashSale() : Response<ApiResponse<List<FlashSaleResponse>>>
}
    ): Response<ApiResponse<OrderResponse>>

    // Address APIs (b? sung thêm t? code 2, d?t cu?i d? gi? nguyên th? t? cu)
    @GET("address/get-all-addresses")
    suspend fun getAllAddresses(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<ApiResponse<PagedResponse<AddressDTO>>>

    @GET("address/{id}")
    suspend fun getAddressById(@Path("id") id: Long): Response<ApiResponse<AddressDTO>>

    @POST("address/create-address")
    suspend fun createAddress(@Body request: AddressRequest): Response<ApiResponse<Any>>

    @PUT("address/update/{id}")
    suspend fun updateAddress(
        @Path("id") id: Long,
        @Body request: AddressRequest
    ): Response<ApiResponse<Any>>

    @DELETE("address/delete/{id}")
    suspend fun deleteAddress(@Path("id") id: Long): Response<ApiResponse<Any>>

    @PATCH("address/set-default/{id}")
    suspend fun setDefaultAddress(@Path("id") id: Long): Response<ApiResponse<Any>>
}
