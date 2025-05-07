package com.store.grocery_store_app.data.api

import com.store.grocery_store_app.data.models.response.CloudinaryResponse
import com.store.grocery_store_app.utils.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface CloudinaryService {
    @Multipart
    @POST("image/upload")
    suspend fun uploadImage(
        @Part file: MultipartBody.Part,
        @Part uploadPreset: MultipartBody.Part
    ): CloudinaryResponse
}
