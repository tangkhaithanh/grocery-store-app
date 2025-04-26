package com.store.grocery_store_app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Bảng màu nhẹ nhàng, phù hợp với thiết kế mẫu
private val LightColorScheme = lightColorScheme(
    primary = DeepTeal,
    onPrimary = Color.White,
    primaryContainer = DeepTealLight,
    onPrimaryContainer = Color.White,

    secondary = AccentGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA5D6A7),
    onSecondaryContainer = DeepTeal,

    tertiary = Color(0xFFFF9800),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFF212121),

    background = BackgroundWhite,
    onBackground = TextPrimary,

    surface = BackgroundWhite,
    onSurface = TextPrimary,

    error = Error,
    onError = Color.White,

    surfaceVariant = BackgroundCream,
    onSurfaceVariant = TextSecondary
)

// Bảng màu tối
private val DarkColorScheme = darkColorScheme(
    primary = DeepTealLight,
    onPrimary = Color.White,
    primaryContainer = DeepTeal,
    onPrimaryContainer = Color.White,

    secondary = AccentGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF388E3C),
    onSecondaryContainer = Color.White,

    tertiary = Color(0xFFFF9800),
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFFF57C00),
    onTertiaryContainer = Color.White,

    background = Color(0xFF121212),
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,

    error = ErrorLight,
    onError = Color.White,

    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFBBBBBB)
)

@Composable
fun GroceryStoreAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Tắt dynamic color để giữ nguyên bảng màu theo thiết kế
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Luôn sử dụng status bar tối
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}