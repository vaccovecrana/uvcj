pluginManagement {
  repositories {
    mavenCentral()
    gradlePluginPortal()
  }
}

include("uvc-jni", "uvc")

project(":uvc-jni").name = "uvc-${System.getProperty("os.name").toLowerCase()}-${System.getProperty("os.arch")}"
