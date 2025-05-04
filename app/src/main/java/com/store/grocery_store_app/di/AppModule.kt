package com.store.grocery_store_app.di

import android.content.Context
import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.api.AuthInterceptor
import com.store.grocery_store_app.data.local.TokenManager
import com.store.grocery_store_app.data.repository.AuthRepository
import com.store.grocery_store_app.data.repository.CategoryRepository
import com.store.grocery_store_app.data.repository.FavoriteProductRepository
import com.store.grocery_store_app.data.repository.OrderRepository
import com.store.grocery_store_app.data.repository.ProductRepository
import com.store.grocery_store_app.data.repository.ReviewRepository
import com.store.grocery_store_app.data.repository.impl.AuthRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.CategoryRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.FavoriteProductRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.OrderRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.ProductRepositoryImpl
import com.store.grocery_store_app.data.repository.impl.ReviewRepositoryImpl
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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(apiService: ApiService, tokenManager: TokenManager): AuthRepository {
        return AuthRepositoryImpl(apiService, tokenManager)
    }

    // Thêm provider cho CategoryRepository
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
    fun provideFavouriteRepository(
        apiService: ApiService
    ): FavoriteProductRepository {
        return FavoriteProductRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideReviewRepository(apiService: ApiService): ReviewRepository {
        return ReviewRepositoryImpl(apiService)
    }
}