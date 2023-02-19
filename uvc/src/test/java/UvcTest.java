import io.vacco.uvc.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.awt.*;
import java.util.function.Consumer;

import static io.vacco.uvc.Uvc.uvc_allocate_frame;
import static io.vacco.uvc.Uvc.uvc_free_frame;
import static j8spec.J8Spec.*;
import static java.lang.Thread.sleep;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class UvcTest {
  static {
    System.setProperty("org.bytedeco.javacpp.logger.debug", "true");

    it("Captures USB camera frames", () -> {
      if (!GraphicsEnvironment.isHeadless()) {
        int w = 640, h = 480;
        var errorHdl = (Consumer<Exception>) Throwable::printStackTrace;
        var win = new DemoFrame();
        win.setSize(640, 480);
        try (var camIo = new UvcCameraIO()) {
          camIo
            .initFirst(w, h, 30, Uvc.UVCFrameFormat.UVC_FRAME_FORMAT_MJPEG)
            .start(frame -> {
              var f = uvc_allocate_frame(w * h);
              camIo.checkStatus(Uvc.uvc_mjpeg2rgb(frame, f), "uvc_mjpeg2rgb");
              win.showImage(UvcImageIO.imageFrom(f));
              uvc_free_frame(f);
            }, errorHdl);
          sleep(1000);
          while (win.isActive()) {
            sleep(1000);
          }
          win.dispose();
        }
      }
    });
  }

}
