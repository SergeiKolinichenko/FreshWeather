@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.ksp)
  alias(libs.plugins.parcelize)
}

android {
  namespace = "info.sergeikolinichenko.myapplication"
  compileSdk = 34

  defaultConfig {
    applicationId = "info.sergeikolinichenko.myapplication"
    minSdk = 26
    targetSdk = 34
    versionCode = 7
    versionName = "1.8"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    kotlinCompilerExtensionVersion = "1.5.11"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {
  implementation(project(":domain"))
  implementation(project(":data"))

  implementation(libs.core.ktx)
  implementation(libs.lifecycle.runtime.ktx)
  implementation(libs.activity.compose)
  implementation(platform(libs.compose.bom))
  implementation(libs.ui)
  implementation(libs.ui.graphics)
  implementation(libs.ui.tooling.preview)
  implementation(libs.material3)

  // MVIKotlin
  implementation(libs.mvikotlin.core)
  implementation(libs.mvikotlin.main)
  implementation(libs.mvikotlin.coroutines)

  // Decompose
  implementation(libs.decompose.core)
  implementation(libs.decompose.jetpack)

  // Dagger 2
  implementation(libs.dagger.core)
  ksp(libs.dagger.compiler)

  implementation(libs.room.core)
  ksp(libs.room.compiler)

  implementation(libs.glide.compose)

  implementation(libs.icons)

  implementation(libs.splashscreen)

  implementation(libs.retrofit.gsonConverter)

  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)

  testImplementation(libs.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.runner)

  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.androidx.core)
}