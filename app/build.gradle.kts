// App module build.gradle
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    // Bỏ KSP vì chúng ta sẽ dùng KAPT cho tất cả
    alias(libs.plugins.hilt) // Sửa thành alias để khớp với version catalog
    id("kotlin-parcelize")
}

android {
    namespace = "com.store.grocery_store_app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.store.grocery_store_app"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.compose.runtime:runtime")  // ĐÚNG

    // Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Dependency Injection - sử dụng KAPT cho Hilt/Dagger
    implementation("com.google.dagger:dagger:2.50")
    implementation(libs.litert.metadata)
    implementation(libs.androidx.runtime.livedata)
    kapt("com.google.dagger:dagger-compiler:2.50")
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // Room Database - chuyển từ KSP sang KAPT để tránh xung đột
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion") // Chuyển từ ksp sang kapt

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.android.material:material:1.6.0")

    implementation("androidx.multidex:multidex:2.0.1")

    // Glide for Compose
    implementation ("com.github.bumptech.glide:compose:1.0.0-alpha.5")

// Coil for Compose (alternative to Glide)
    implementation ("io.coil-kt:coil-compose:2.4.0")
    //Upload to Cloudinary
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("com.cloudinary:cloudinary-android:2.0.0")
    // https://mvnrepository.com/artifact/com.google.maps.android/maps-compose
    implementation("com.google.maps.android:maps-compose:5.0.0")
    // https://mvnrepository.com/artifact/com.google.android.gms/play-services-maps
    implementation("com.google.android.gms:play-services-maps:17.0.0")
}