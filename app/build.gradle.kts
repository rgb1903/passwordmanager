plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.hiltAndroid)
    alias(libs.plugins.navigationSafeArgs)
    id("kotlin-parcelize")
    id("kotlin-kapt")

}

android {
    namespace = "com.example.passwordmanager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.passwordmanager"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerview)


    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.ktx)

    // Coroutines
    implementation(libs.coroutines.android)

    //hilt

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.fragment.ktx)
    implementation(libs.activity.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}