package com.store.grocery_store_app.data.repository.impl

import android.content.Context
import android.net.Uri
import com.store.grocery_store_app.data.api.CloudinaryService
import com.store.grocery_store_app.data.models.response.CloudinaryResponse
import com.store.grocery_store_app.data.repository.CloudinaryRepository
import com.store.grocery_store_app.utils.FileUtil
import com.store.grocery_store_app.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Named
class CloudinaryRepositoryImpl @Inject constructor(
    @Named("cloudinaryRetrofit") private val cloudinaryService: CloudinaryService,
    @ApplicationContext private val context: Context
) : CloudinaryRepository {

    override suspend fun uploadImage(imageUri: Uri): Flow<Resource<String>> = flow {
        emit(Resource.Loading())

        try {
            // Chuyển Uri -> File
            val imageFile: File = FileUtil.from(context, imageUri)

            // Tạo Multipart
            val requestFile = imageFile
                .asRequestBody("image/*".toMediaTypeOrNull())

            val multipartBody = MultipartBody.Part.createFormData(
                "file", imageFile.name, requestFile
            )

            val uploadPreset = MultipartBody.Part.createFormData("upload_preset", "grocery_store_upload")

            // Gọi API Cloudinary
            val response: CloudinaryResponse = cloudinaryService.uploadImage(multipartBody, uploadPreset)

            emit(Resource.Success(response.url)) // URL trả về
        } catch (e: Exception) {
            emit(Resource.Error("Upload failed: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }
}