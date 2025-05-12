package com.store.grocery_store_app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.store.grocery_store_app.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
// Lớp này quản lý thông tin của user khi họ đăng nhập vao he thong
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constants.DATASTORE_NAME)
@Singleton
class TokenManager  @Inject constructor(@ApplicationContext private val context: Context){
    private val accessTokenKey = stringPreferencesKey(Constants.ACCESS_TOKEN_KEY)
    private val refreshTokenKey = stringPreferencesKey(Constants.REFRESH_TOKEN_KEY)
    private val userEmailKey = stringPreferencesKey(Constants.USER_EMAIL_KEY)
    private val userNameKey = stringPreferencesKey(Constants.USER_NAME_KEY)
    private val userImageKey = stringPreferencesKey(Constants.USER_IMAGE_KEY)
    private val userIdKey = longPreferencesKey(Constants.USER_ID_KEY)
    // Lưu thông tin đăng nhập
    suspend fun saveUserInfo(
        accessToken: String,
        refreshToken: String,
        email: String,
        fullName: String,
        imageUrl: String?,
        userId: Long
    ) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
            preferences[userEmailKey] = email
            preferences[userNameKey] = fullName
            preferences[userImageKey] = imageUrl ?: ""
            preferences[userIdKey] = userId
        }
    }

    // Cập nhật access token
    suspend fun updateAccessToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[accessTokenKey] = token
        }
    }

    // Lấy access token
    val accessToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[accessTokenKey]
        }

    // Lấy refresh token
    val refreshToken: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[refreshTokenKey]
        }

    // Lấy email
    val userEmail: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[userEmailKey]
        }

    // Lấy tên người dùng
    val userName: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[userNameKey]
        }

    // Lấy URL hình ảnh
    val userImage: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[userImageKey]
        }
    val userId: Flow<Long?>
        get() = context.dataStore.data.map { preferences ->
            preferences[userIdKey]
        }

    // Xóa tất cả thông tin đăng nhập (đăng xuất)
    suspend fun clearUserInfo() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}