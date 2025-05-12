package com.store.grocery_store_app.data.repository.impl

import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.request.UpdateUserRequest
import com.store.grocery_store_app.data.models.response.UserDTO
import com.store.grocery_store_app.data.repository.UserRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository{
    override suspend fun getUserById(id: Long): Flow<Resource<UserDTO>> = flow{
        emit(Resource.Loading())
        try {
            val response = apiService.getUserById(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy thông tin người dùng thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun updateUser(id: Long, request: UpdateUserRequest): Flow<Resource<UserDTO>> =
        flow {
            emit(Resource.Loading())
            try {
                val response = apiService.updateUser(id, request)
                if (response.isSuccessful) {
                    response.body()?.let { apiResponse ->
                        if (apiResponse.success && apiResponse.data != null) {
                            emit(Resource.Success(apiResponse.data))
                        } else {
                            emit(Resource.Error(apiResponse.message))
                        }
                    } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
                } else {
                    emit(Resource.Error("Cập nhật thông tin thất bại: ${response.message()}"))
                }
            } catch (e: HttpException) {
                emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
            } catch (e: IOException) {
                emit(Resource.Error("Không thể kết nối đến máy chủ"))
            } catch (e: Exception) {
                emit(Resource.Error("Lỗi không xác định: ${e.message}"))
            }
        }
}