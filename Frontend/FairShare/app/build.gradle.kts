plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.priyanshparekh.fairshare"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.priyanshparekh.fairshare"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
//        manifestPlaceholders = ["appAuthRedirectScheme": ""]

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.sdp.android)
    implementation(libs.androidx.core.splashscreen)

    // AWS Cognito
//    implementation(libs.appauth)
    implementation(libs.aws.android.sdk.core)
    implementation(libs.aws.android.sdk.cognitoidentityprovider)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
//    implementation(libs.aws.android.sdk.s3)


//    implementation("androidx.activity:activity-compose:1.8.2")
//    implementation("androidx.compose.ui:ui:1.5.4")
//    implementation("androidx.compose.material3:material3:1.1.2")
    implementation(libs.androidx.runtime.livedata)

// Lifecycle + ViewModel for Compose
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.androidx.navigation.fragment.ktx)


    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.converter.scalars)
    implementation(libs.okhttp.urlconnection)

    implementation(libs.mpandroidchart)
    implementation(libs.dotsindicator)

    implementation(libs.firebase.messaging)
}