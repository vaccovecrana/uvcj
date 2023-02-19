package io.vacco.uvc;

import org.bytedeco.javacpp.Pointer;
import java.io.Closeable;
import java.util.function.Consumer;

import static io.vacco.uvc.Uvc.*;

public class UvcCameraIO implements Closeable {

  private final UVCDevice       dev = new UVCDevice();
  private final UVCDeviceHandle devh = new UVCDeviceHandle();
  private final UVCContext      ctx = new UVCContext();
  private final UVCStreamCtrl   sctrl = new UVCStreamCtrl();

  private UVCFrameCallback frameCb;

  public void checkStatus(int status, String uvm) {
    if (status < UVCError.UVC_SUCCESS) {
      throw new IllegalStateException(String.format(
        "UVC error: [%s, %d]", uvm, status
      ));
    }
  }

  public void checkStatus(int status, String uvm, Consumer<Exception> onError) {
    try {
      checkStatus(status, uvm);
    } catch (Exception e) {
      onError.accept(e);
    }
  }

  public UvcCameraIO initFirst(int width, int height, int fps, int uvcFmt) {
    checkStatus(uvc_init(ctx, null), "uvc_init");
    checkStatus(uvc_find_device(ctx, dev, 0, 0, null), "uvc_find_device");
    checkStatus(uvc_open(dev, devh), "uvc_open");
    checkStatus(
      uvc_get_stream_ctrl_format_size(
        devh, sctrl, uvcFmt, width, height, fps
      ), "uvc_get_stream_ctrl_format_size"
    );
    return this;
  }

  public UvcCameraIO start(Consumer<UVCFrame> onFrame, Consumer<Exception> onError) {
    this.frameCb = new UVCFrameCallback() {
      @Override public void call(UVCFrame frame, Pointer user_ptr) {
        try {
          onFrame.accept(frame);
        } catch (Exception e) {
          onError.accept(e);
        }
      }
    };
    var t = new Thread(() -> {
      uvc_set_focus_auto(devh, (byte) 0);
      checkStatus(uvc_start_streaming(devh, sctrl, this.frameCb, (byte) 0), "uvc_start_streaming", onError);
    });
    t.setName(getClass().getSimpleName());
    t.start();
    return this;
  }

  public UVCFormatDesc getFormatDescriptor() {
    return Uvc.uvc_get_format_descs(devh);
  }

  @Override public void close() {
    if (this.frameCb != null) {
      this.frameCb.close();
    }
    uvc_stop_streaming(devh);
    uvc_close(devh);
    uvc_unref_device(dev);
    uvc_exit(ctx);
  }

}
