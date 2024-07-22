@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.ksp)
  alias(libs.plugins.parcelize)
  alias(libs.plugins.androidx.room)
}

android {
  namespace = "info.sergeikolinichenko.data"
  compileSdk = 34

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
    val key = property("apikey")?.toString() ?: error("Set apikey property in local.properties")
    buildConfigField("String", "API_KEY", key)
    room {
      schemaDirectory("$projectDir/schemas")
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
    buildConfig = true
  }
}

dependencies {
  implementation(project(":domain"))

  implementation(libs.dagger.core)
  implementation(libs.androidx.junit.ktx)
  ksp(libs.dagger.compiler)

  implementation(libs.room.core)
  ksp(libs.room.compiler)

  implementation(libs.retrofit.core)
  implementation(libs.retrofit.gsonConverter)
  implementation(libs.retrofit.logging.interceptor)

  implementation(libs.google.gson)

  testImplementation(libs.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.runner)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.androidx.core)

  testImplementation(libs.robolectric)
}