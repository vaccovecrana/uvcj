configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  sharedLibrary(true, false)
}

val api by configurations

dependencies {
  api("org.bytedeco:javacpp:1.5.8")
}
