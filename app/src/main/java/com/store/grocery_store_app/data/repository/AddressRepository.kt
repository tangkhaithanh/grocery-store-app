package com.store.grocery_store_app.data.repository
import com.store.grocery_store_app.data.models.request.AddressRequest
import com.store.grocery_store_app.data.models.response.AddressDTO
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow
interface AddressRepository {
    suspend fun getAllAddresses(page: Int = 0, size: Int = 10): Flow<Resource<List<AddressDTO>>>
    suspend fun getAddressById(id: Long): Flow<Resource<AddressDTO>>
    suspend fun createAddress(request: AddressRequest): Flow<Resource<Unit>>
    suspend fun updateAddress(id: Long, request: AddressRequest): Flow<Resource<Unit>>
    suspend fun deleteAddress(id: Long): Flow<Resource<Unit>>
    suspend fun setDefaultAddress(id: Long): Flow<Resource<Unit>>
}