import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp.google)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.gms)
}

android {
    namespace = libs.versions.appId.get()
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = libs.versions.appId.get()
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    flavorDimensions.add("environment")
    productFlavors {
        create("dev") {
            resValue("string", "app_name", "GemLens AI-DEV")
            buildConfigField("String", "BASE_URL", "\"https://generativelanguage.googleapis.com/\"")
        }

        create("production") {
            resValue("string", "app_name", "GemLens AI")
            buildConfigField("String", "BASE_URL", "\"https://generativelanguage.googleapis.com/\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_24
        targetCompatibility = JavaVersion.VERSION_24
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(JavaVersion.VERSION_24.toString()))
        }
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    /**
     ******************************* Android Common Component***************************************
     **/
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.timber)

    /**
     ******************************* Image Loading *************************************************
     **/
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    /**
     ******************************* ViewModel and LiveData ****************************************
     **/
    implementation(libs.lifecycle.viewmodel.ktx)

    /**
     ******************************* DI ************************************************************
     **/
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    /**
     ******************************* Network-KTOR **************************************************
     **/
    implementation(libs.ktor.core)
    implementation(libs.ktor.android)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.kotlinx.json)
    implementation(libs.ktor.logging)
    implementation(libs.ktor.okhttp)

    /**
     ******************************* AI **************************************************
     **/
    implementation(libs.generativeai)

    /**
     ******************************* FIREBASE  **************************************************
     **/
    implementation(platform(libs.platform.firebase))
    implementation(libs.firebase.remote.config)
    implementation(libs.firebase.analytic)

    /**
     ******************************* DATABASE/DATA-STORE **************************************************
     **/
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    /**
     ******************************* Unit Testing ************************************
     **/
    debugImplementation(libs.androidx.ui.tooling)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.koin.test)

}