package com.store.grocery_store_app

import AuthNavGraph
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.store.grocery_store_app.ui.theme.GroceryStoreAppTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity chính của ứng dụng
 * Thiết lập giao diện và điều hướng
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Áp dụng theme cho toàn bộ ứng dụng
            GroceryStoreAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Tạo NavController cho điều hướng
                    val navController = rememberNavController()
                    // Thiết lập NavHost chính
                    AuthNavGraph(navController = navController)
                }
            }
        }
    }
}