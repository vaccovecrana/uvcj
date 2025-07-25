plugins { id("io.vacco.oss.gitflow") version "1.8.2" }

group = "io.vacco.uvcj"
version = "0.0.7"

configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  sharedLibrary(true, false)
}

tasks.named<ProcessResources>("processResources") {
  from("src/main/c/libuvc_jni.so") { into("io/vacco/uvc") }
  from("src/main/c/libuvc_jni.dylib") { into("io/vacco/uvc") }
}

println("${System.getProperty("os.name")}-${System.getProperty("os.arch")}")
