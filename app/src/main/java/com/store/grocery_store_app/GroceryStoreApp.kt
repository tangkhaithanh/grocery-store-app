package com.store.grocery_store_app
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp
@HiltAndroidApp
class GroceryStoreApp : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        // Không đặt code nặng ở đây
    }
}