import io.vacco.uvc.Uvc;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.junit.runner.RunWith;

import javax.swing.*;
import java.awt.*;

import static io.vacco.uvc.Uvc.*;
import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class UvcTest {
  static {
    System.setProperty("org.bytedeco.javacpp.logger.debug", "true");

    it("Captures USB camera frames", () -> {
      UVCDevice dev = new Uvc.UVCDevice();
      UVCDeviceHandle devh = new Uvc.UVCDeviceHandle();
      UVCContext ctx = new Uvc.UVCContext();
      UVCStreamCtrl sctrl = new Uvc.UVCStreamCtrl();

      /* Abrimos la cámara */
      System.out.println("Init Result: " + Uvc.uvc_init(ctx, null));
      System.out.println("Find Result: " + Uvc.uvc_find_device(ctx, dev, 0, 0, null)); // Detección Automática
      System.out.println("Open Result: " + Uvc.uvc_open(dev, devh));

      // Obtención de la info de la cámara
      PointerPointer<Uvc.UVCDeviceDescriptor> descp = new PointerPointer<>(new Uvc.UVCDeviceDescriptor());
      System.out.println(Uvc.uvc_get_device_descriptor(dev, descp));
      UVCDeviceDescriptor desc = descp.get(Uvc.UVCDeviceDescriptor.class);
      // System.out.println("Serial Number: " + desc.serialNumber().getString());
      // System.out.println("Manufacturer: " + desc.manufacturer());
      // System.out.println("Product Description: " + desc.product().getString());
      Uvc.uvc_free_device_descriptor(desc);

      // Obtención de la descripción del formato
      UVCFormatDesc frmtDesc = Uvc.uvc_get_format_descs(devh);

      System.out.println("Descriptor Subtype: " + frmtDesc.bDescriptorSubtype());
      System.out.println("Bits Per Pixel: " + frmtDesc.bBitsPerPixel());
      System.out.println("Aspect Ratio X: " + frmtDesc.bAspectRatioX());
      System.out.println("Aspect Ratio Y: " + frmtDesc.bAspectRatioY());
      System.out.println("Default Frame Index: " + frmtDesc.bDefaultFrameIndex());
      System.out.println("Frame Descriptors: " + frmtDesc.bNumFrameDescriptors());
      System.out.println("Interlace Flags: " + frmtDesc.bmInterlaceFlags());

      int res = Uvc.uvc_get_stream_ctrl_format_size(devh, sctrl, UVCFrameFormat.UVC_FRAME_FORMAT_MJPEG, 640, 480, 30);
      System.out.println("Get Stream Control Result: " + res);

      if (res != 0) {
        System.out.println("Configuración no Permitida");
        return;
      }

      System.out.println("Frame Index: " + sctrl.bFrameIndex());
      System.out.println("Max Video Frame Buffer Size: " + sctrl.dwMaxVideoFrameSize());
      System.out.println("Frame Interval: " + sctrl.dwFrameInterval());
      System.out.println("Interface Number: " + sctrl.bInterfaceNumber());

      final DemoFrame windows = new DemoFrame("Test");
      windows.setSize(640, 480);

      UVCFrameCallback callback = new UVCFrameCallback() {

        @Override
        public void call(UVCFrame frame, Pointer user_ptr) {
          try {
            UVCFrame cframe = Uvc.uvc_allocate_frame(frame.width() * frame.height());
            int convres = Uvc.uvc_mjpeg2rgb(frame, cframe);
            if (convres != 0) {
              return;
            }

            windows.showImage(Utils.getImage(cframe));

            Uvc.uvc_free_frame(cframe);
          } catch (Exception e) {
            e.printStackTrace(System.out);
          }
        }
      };

      Uvc.uvc_set_focus_auto(devh, (byte) 0);
      Uvc.uvc_start_streaming(devh, sctrl, callback, (byte) 0);

      // Thread.sleep(15000); /* 5 segundos de video */
      while (windows.isActive()) {
        Thread.sleep(1000);
      }

      System.out.println("Closing device.");

      Uvc.uvc_stop_streaming(devh);
      windows.dispose();

      /* Cerramos la cámara */
      Uvc.uvc_close(devh);
      Uvc.uvc_unref_device(dev);
      Uvc.uvc_exit(ctx);
    });
  }

  static class DemoFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = 7534330708637208215L;
    private JPanel imgPanel;

    DemoFrame(String title) {
      super(title);
      initComponents();
      this.setVisible(true);
      ((ImagePanel) this.imgPanel).createBuffer();
    }

    private void initComponents() {

      imgPanel = new ImagePanel();

      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      setName("DemoFrame"); // NOI18N
      getContentPane().setLayout(new GridLayout(1, 1));

      imgPanel.setName("imgPanel");

      GroupLayout imgPanelLayout = new GroupLayout(imgPanel);
      imgPanel.setLayout(imgPanelLayout);
      imgPanelLayout
        .setHorizontalGroup(imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 576, Short.MAX_VALUE));
      imgPanelLayout
        .setVerticalGroup(imgPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addGap(0, 445, Short.MAX_VALUE));

      getContentPane().add(imgPanel);

      pack();
    }

    public void showImage(Image img) {
      ((ImagePanel) this.imgPanel).renderImage(img);
    }

  }
}
