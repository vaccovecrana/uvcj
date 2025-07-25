package io.vacco.uvc;

public class UvcTest {

  public static void main(String[] args) {
    long ctx = Uvc.initContext();
    if (ctx == 0) {
      System.err.println("Failed to initialize UVC context");
      return;
    }
    System.out.println("UVC initialized");

    long[] devs = Uvc.getDeviceList(ctx);
    if (devs == null || devs.length == 0) {
      System.err.println("No cameras found");
      Uvc.exitContext(ctx);
      return;
    }

    int numCameras = devs.length;
    long firstCamera = 0;
    for (int i = 0; i < numCameras; i++) {
      var desc = Uvc.getDeviceDescriptor(devs[i]);
      System.out.printf("Camera %d: %s %s (serial %s)%n",
        i, desc.manufacturer != null ? desc.manufacturer : "?",
        desc.product != null ? desc.product : "?",
        desc.serialNumber != null ? desc.serialNumber : "?");
      if (i == 0) firstCamera = devs[i];
    }

    long devh = Uvc.openDevice(firstCamera);
    if (devh == 0) {
      System.err.println("Failed to open first camera");
      Uvc.freeDeviceList(devs);
      Uvc.exitContext(ctx);
      return;
    }
    System.out.println("Opened first camera");

    var formats = Uvc.getFormatDescriptors(devh);
    for (var fmt : formats) {
      System.out.println(fmt);
    }

    long ctrl = Uvc.getStreamCtrlFormatSize(devh, Uvc.UVC_FRAME_FORMAT_MJPEG, 640, 480, 30);
    final boolean[] gotFrame = {false};
    var callback = (UvcFrameCallback) (frame, userData) -> {
      System.out.printf(
        "Frame received: %d x %d, format %d, size %d%n",
        frame.width, frame.height, frame.format, frame.data.length
      );
      gotFrame[0] = true;
    };

    Uvc.startStreaming(devh, ctrl, callback, null, 0);
    while (!gotFrame[0]) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
    Uvc.stopStreaming(devh);
    System.out.println("Got frame");

    Uvc.closeDevice(devh);
    Uvc.freeDeviceList(devs);
    Uvc.exitContext(ctx);
  }

}
