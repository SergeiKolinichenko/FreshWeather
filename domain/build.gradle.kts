@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
  id("java-library")
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.ksp)
}

java {
  sourceCompatibility = JavaVersion.VERSION_17
  targetCompatibility = JavaVersion.VERSION_17
}

dependencies {

  implementation(libs.dagger.core)
  ksp(libs.dagger.compiler)

  implementation(libs.kotlinx.coroutines.android)

  testImplementation(libs.junit4)
  testImplementation(libs.mockito.core)
  testImplementation(libs.mockito.kotlin)
}