plugins {
  id("java-library")
  alias(libs.plugins.org.jetbrains.kotlin.jvm)
  alias(libs.plugins.ksp)
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<Test> {
  jvmArgs("-XX:+EnableDynamicAgentLoading")
}

dependencies {

  implementation(libs.dagger.core)
  ksp(libs.dagger.compiler)

  implementation(libs.kotlinx.coroutines.android)

  testImplementation(libs.junit4)
  testImplementation(libs.mockito.inline)
  testImplementation(libs.mockito.kotlin)
  testImplementation(libs.coroutines.test)
}