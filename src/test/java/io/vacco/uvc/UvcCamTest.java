package io.vacco.uvc;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UvcCamTest {

  public static void main(String[] args) {
    long ctx = Uvc.initContext();
    long[] devs = Uvc.getDeviceList(ctx);
    long devh = Uvc.openDevice(devs[0]);
    long ctrl = Uvc.getStreamCtrlFormatSize(devh, Uvc.UVC_FRAME_FORMAT_YUYV, 640, 480, 30);

    var frame = new JFrame("UVC Camera");
    frame.setSize(640, 480);
    var label = new JLabel();
    frame.add(label);
    frame.setVisible(true);

    Uvc.startStreaming(devh, ctrl, (f, ud) -> {
      var img = f.toBufferedImage();
      SwingUtilities.invokeLater(() -> label.setIcon(new ImageIcon(img)));
    }, null, 0);

    frame.addWindowListener(new WindowAdapter() {
      @Override public void windowClosing(WindowEvent e) {
        Uvc.stopStreaming(devh);
        Uvc.closeDevice(devh);
        Uvc.freeDeviceList(devs);
        Uvc.exitContext(ctx);
        System.out.println("uvc stopped");
      }
    });
  }

}