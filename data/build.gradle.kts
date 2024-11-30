plugins {
  alias(libs.plugins.androidLibrary)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.ksp)
  alias(libs.plugins.parcelize)
  alias(libs.plugins.androidx.room)
}

android {
  namespace = "info.sergeikolinichenko.data"
  compileSdk = 35

  defaultConfig {
    minSdk = 26

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")

    val apikeyVisualcrossing = property("apikeyVisualcrossing")?.toString() ?: error("Set apikey property in local.properties")
    buildConfigField("String", "API_KEY_VISUALCROSSING", apikeyVisualcrossing)

    val apikeySearch = property("apikeySearch")?.toString() ?: error("Set apikey property in local.properties")
    buildConfigField("String", "SEARCH_API_KEY", apikeySearch)

    room {
      schemaDirectory("$projectDir/schemas")
    }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
  }
  kotlinOptions {
    jvmTarget = "21"
    freeCompilerArgs = listOf(
      "-Xstring-concat=inline"
    )
  }
  buildFeatures {
    buildConfig = true
  }
}

tasks.withType<Test> {
  jvmArgs("-XX:+EnableDynamicAgentLoading")
}

dependencies {
  implementation(project(":domain"))

  implementation(libs.dagger.core)
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

  testImplementation(libs.androidx.junit.ktx)
}