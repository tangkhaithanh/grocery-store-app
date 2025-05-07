package com.store.grocery_store_app.data.repository

import android.net.Uri
import com.store.grocery_store_app.data.models.response.CloudinaryResponse
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface CloudinaryRepository {
    suspend fun uploadImage(imageUri: Uri): Flow<Resource<String>>
}