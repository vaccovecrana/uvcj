configure<io.vacco.oss.gitflow.GsPluginProfileExtension> {
  addJ8Spec()
  sharedLibrary(true, false)
}

dependencies {
  /**
   * TODO this is a platform specific provided dependency
   * This is fine for now, but change it when other platforms are needed.
   */
  implementation(project(":uvc-jni-linux-x86_64"))
}
