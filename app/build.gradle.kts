import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinAndroid)
  alias(libs.plugins.ksp)
  alias(libs.plugins.parcelize)
  alias(libs.plugins.compose.compiler)
}

android {
  namespace = "info.sergeikolinichenko.myapplication"
  compileSdk = 34

  defaultConfig {
    applicationId = "info.sergeikolinichenko.myapplication"
    minSdk = 26
    targetSdk = 34
    versionCode = 10
    versionName = "2.0.2"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }
    signingConfig = signingConfigs.getByName("debug")
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
    freeCompilerArgs = listOf(
      "-Xstring-concat=inline"
    )
  }

  buildFeatures { compose = true }

  packaging {
    resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
  }

  @Suppress("UnstableApiUsage")
  testOptions {
    unitTests.isReturnDefaultValues = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.bom.get()
  }

}
composeCompiler {
  featureFlags = setOf(
    ComposeFeatureFlag.StrongSkipping.disabled(),
    ComposeFeatureFlag.OptimizeNonSkippingGroups
  )
  reportsDestination = layout.buildDirectory.dir("compose_compiler")
  stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
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

  implementation(libs.icons)

  implementation(libs.splashscreen)

  implementation(libs.retrofit.gsonConverter)

  testImplementation(libs.junit4)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.runner)

  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.coroutines.test)
  testImplementation(libs.androidx.core)

  testImplementation(libs.robolectric)

  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.ui.test.junit4)
  debugImplementation(libs.ui.tooling)
  debugImplementation(libs.ui.test.manifest)

  testImplementation(libs.androidx.junit.ktx)
}