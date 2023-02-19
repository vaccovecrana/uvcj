import org.bytedeco.javacpp.tools.Builder;

public class UvcGen {
  public static void main(String[] args) throws Exception {
    Builder.main(new String[] {
      "io.vacco.uvc.Uvc",
      "-nodelete",
      "-d", "./uvc-jni-linux-x86_64/src/main/java/io/vacco/uvc"
    });
  }
}
