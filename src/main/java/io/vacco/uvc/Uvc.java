package io.vacco.uvc;

import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Uvc {

  /* See https://github.com/libuvc/libuvc/blob/master/include/libuvc/libuvc.h#L59 */
  public static final int UVC_FRAME_FORMAT_YUYV  = 3;
  public static final int UVC_FRAME_FORMAT_RGB   = 5;
  public static final int UVC_FRAME_FORMAT_BGR   = 6;
  public static final int UVC_FRAME_FORMAT_MJPEG = 7;
  public static final int UVC_FRAME_FORMAT_GRAY8 = 8;

  native public static long initContext();
  native public static void exitContext(long ctx);

  native public static long[] getDeviceList(long ctx);
  native public static void freeDeviceList(long[] devs);

  native public static UvcDeviceDescriptor getDeviceDescriptor(long dev);

  native public static long openDevice(long dev);
  native public static void closeDevice(long devh);

  native public static long getStreamCtrlFormatSize(long devh, int format, int width, int height, int fps);

  native public static void startStreaming(long devh, long ctrl, UvcFrameCallback callback, Object userData, int flags);
  native public static void stopStreaming(long devh);

  native public static void setFocusAuto(long devh, boolean autoFocus);

  native public static UvcFormatDesc[] getFormatDescriptors(long devh);

  private static void load(String path) {
    try (var is = Uvc.class.getResourceAsStream(path)) {
      if (is == null) {
        throw new RuntimeException(path + " not found in classpath");
      }
      var tempFile = Files.createTempFile("libuvc", ".jni");
      Files.copy(is, tempFile, StandardCopyOption.REPLACE_EXISTING);
      System.load(tempFile.toAbsolutePath().toString());
    } catch (Exception e) {
      throw new RuntimeException("Failed to load libuvc_jni", e);
    }
  }

  static {
    var osName = System.getProperty("os.name").toLowerCase();
    var osArch = System.getProperty("os.arch").toLowerCase();
    if (osName.contains("linux") && osArch.equals("amd64")) {
      load("/io/vacco/uvc/libuvc_jni.so");
    } else if (osName.contains("mac os x") && osArch.equals("x86_64")) {
      load("/io/vacco/uvc/libuvc_jni.dylib");
    }
  }

}