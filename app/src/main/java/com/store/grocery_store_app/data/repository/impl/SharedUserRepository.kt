package com.store.grocery_store_app.data.repository.impl

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedUserRepository @Inject constructor() {
    private val _userUpdatedFlow = MutableSharedFlow<Boolean>(replay = 0)
    val userUpdatedFlow: SharedFlow<Boolean> = _userUpdatedFlow.asSharedFlow()

    suspend fun notifyUserUpdated() {
        _userUpdatedFlow.emit(true)
    }
}