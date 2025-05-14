package com.store.grocery_store_app.data.repository.impl

import com.store.grocery_store_app.data.api.ApiService
import com.store.grocery_store_app.data.models.request.AddressRequest
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.data.repository.AddressRepository
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AddressRepository {

    override suspend fun getAllAddresses(page: Int, size: Int): Flow<Resource<List<AddressDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAllAddresses(page, size)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        val addresses = apiResponse.data.content
                        emit(Resource.Success(addresses))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy danh sách địa chỉ thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun getAddressById(id: Long): Flow<Resource<AddressDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getAddressById(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success && apiResponse.data != null) {
                        emit(Resource.Success(apiResponse.data))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Lấy thông tin địa chỉ thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun createAddress(request: AddressRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.createAddress(request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(Unit))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Tạo địa chỉ thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun updateAddress(id: Long, request: AddressRequest): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.updateAddress(id, request)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(Unit))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Cập nhật địa chỉ thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun deleteAddress(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.deleteAddress(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(Unit))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Xóa địa chỉ thất bại: ${response.message()}"))
            }
        } catch (e: HttpException) {
            emit(Resource.Error("Lỗi HTTP: ${e.message()}"))
        } catch (e: IOException) {
            emit(Resource.Error("Không thể kết nối đến máy chủ"))
        } catch (e: Exception) {
            emit(Resource.Error("Lỗi không xác định: ${e.message}"))
        }
    }

    override suspend fun setDefaultAddress(id: Long): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.setDefaultAddress(id)
            if (response.isSuccessful) {
                response.body()?.let { apiResponse ->
                    if (apiResponse.success) {
                        emit(Resource.Success(Unit))
                    } else {
                        emit(Resource.Error(apiResponse.message))
                    }
                } ?: emit(Resource.Error("Phản hồi rỗng từ server"))
            } else {
                emit(Resource.Error("Đặt địa chỉ mặc định thất bại: ${response.message()}"))
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