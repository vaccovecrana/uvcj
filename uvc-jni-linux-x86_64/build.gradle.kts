configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  sharedLibrary(true, false)
}

val api by configurations

dependencies {
  /*
   * TODO JNI binary libjnijavacpp.so is bundled with this library
   * because Maven native dependency management is a nightmare :P.
   * Needs to be upgraded alongside the javacpp jar.
   */
  api("org.bytedeco:javacpp:1.5.8")
}
