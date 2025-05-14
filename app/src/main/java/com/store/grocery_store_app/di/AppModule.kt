package com.store.grocery_store_app.di

import android.content.Context
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.api.AuthInterceptor
import com.store.grocery_store_app.data.api.CloudinaryService
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.repository.AddressRepository
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.data.repository.CartRepository
import com.store.grocery_store_app.data.repository.CategoryRepository
import com.store.grocery_store_app.data.repository.CloudinaryRepository
import com.store.grocery_store_app.data.repository.FavoriteProductRepository
import com.store.grocery_store_app.data.repository.FlashSaleRepository
import com.store.grocery_store_app.data.repository.OrderItemRepository
import com.store.grocery_store_app.data.repository.OrderRepository
import com.store.grocery_store_app.data.repository.ProductRepository
import com.store.grocery_store_app.data.repository.ReviewRepository
import com.store.grocery_store_app.data.repository.UserRepository
import com.store.grocery_store_app.data.repository.VoucherRepository
import com.store.grocery_store_app.data.repository.impl.AddressRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.AuthRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.CartRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.CategoryRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.CloudinaryRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.FavoriteProductRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.FlashSaleRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.OrderItemRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.OrderRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.ProductRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.ReviewRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.SharedUserRepository
import com.store.grocery_store_app.data.repository.impl.UserRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.VoucherRepositoryImpl
import com.store.grocery_store_app.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.annotation.Signed
import javax.inject.Named
import javax.inject.Singleton
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager {
        return TokenManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("apiRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(@Named("apiRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
    @Provides
    @Singleton
    @Named("cloudinaryOkHttpClient")
    fun provideCloudinaryOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // KHÔNG thêm AuthInterceptor
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    @Provides
    @Singleton
    @Named("cloudinaryRetrofit")
    fun provideCloudinaryRetrofit(@Named("cloudinaryOkHttpClient") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL_CLOUDINARY)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCloudinaryService(@Named("cloudinaryRetrofit") retrofit: Retrofit): CloudinaryService {
        return retrofit.create(CloudinaryService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImpl(apiService, tokenManager)
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(apiService: ApiService): CategoryRepository {
        return CategoryRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideProductRepository(apiService: ApiService): ProductRepository {
        return ProductRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideOrderRepository(apiService: ApiService): OrderRepository {
        return OrderRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideFavouriteRepository(apiService: ApiService): FavoriteProductRepository {
        return FavoriteProductRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(apiService: ApiService): ReviewRepository {
        return ReviewRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideOrderItemRepository(apiService: ApiService): OrderItemRepository {
        return OrderItemRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideCloudinaryRepository(cloudinaryService: CloudinaryService, @ApplicationContext context: Context): CloudinaryRepository {
        return CloudinaryRepositoryImpl(cloudinaryService, context)
    }

    @Provides
    @Singleton
    fun provideCartRepository(apiService: ApiService): CartRepository {
        return CartRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideVoucherRepository(apiService: ApiService): VoucherRepository {
        return VoucherRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): UserRepository {
        return UserRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideSharedUserRepository(): SharedUserRepository {
        return SharedUserRepository()
    }

    @Provides
    @Singleton
    fun provideFlashSaleRepository(apiService: ApiService): FlashSaleRepository {
        return FlashSaleRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideAddressRepository(apiService: ApiService): AddressRepository {
        return AddressRepositoryImpl(apiService)
    }
}
