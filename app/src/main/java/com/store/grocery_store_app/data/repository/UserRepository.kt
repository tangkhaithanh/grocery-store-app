package com.store.grocery_store_app.data.repository

import com.store.grocery_store_app.data.models.request.UpdateUserRequest
import com.store.grocery_store_app.data.models.response.UserDTO
import com.store.grocery_store_app.utils.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserById(id: Long): Flow<Resource<UserDTO>>
    suspend fun updateUser(id: Long, request: UpdateUserRequest): Flow<Resource<UserDTO>>
}