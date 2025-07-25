package io.vacco.uvc;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UvcFrame {

  public int width;
  public int height;
  public int format;
  public byte[] data;

  private static final byte[] DEFAULT_DHT = {
    (byte)0xff, (byte)0xc4, (byte)0x01, (byte)0xa2, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x05, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01,
    (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x02,
    (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09, (byte)0x0a, (byte)0x0b, (byte)0x01, (byte)0x00, (byte)0x03,
    (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0x00,
    (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09,
    (byte)0x0a, (byte)0x0b, (byte)0x10, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x03, (byte)0x03, (byte)0x02, (byte)0x04, (byte)0x03, (byte)0x05,
    (byte)0x05, (byte)0x04, (byte)0x04, (byte)0x00, (byte)0x00, (byte)0x01, (byte)0x7d, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x00, (byte)0x04,
    (byte)0x11, (byte)0x05, (byte)0x12, (byte)0x21, (byte)0x31, (byte)0x41, (byte)0x06, (byte)0x13, (byte)0x51, (byte)0x61, (byte)0x07, (byte)0x22,
    (byte)0x71, (byte)0x14, (byte)0x32, (byte)0x81, (byte)0x91, (byte)0xa1, (byte)0x08, (byte)0x23, (byte)0x42, (byte)0xb1, (byte)0xc1, (byte)0x15,
    (byte)0x52, (byte)0xd1, (byte)0xf0, (byte)0x24, (byte)0x33, (byte)0x62, (byte)0x72, (byte)0x82, (byte)0x09, (byte)0x0a, (byte)0x16, (byte)0x17,
    (byte)0x18, (byte)0x19, (byte)0x1a, (byte)0x25, (byte)0x26, (byte)0x27, (byte)0x28, (byte)0x29, (byte)0x2a, (byte)0x34, (byte)0x35, (byte)0x36,
    (byte)0x37, (byte)0x38, (byte)0x39, (byte)0x3a, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47, (byte)0x48, (byte)0x49, (byte)0x4a,
    (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57, (byte)0x58, (byte)0x59, (byte)0x5a, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66,
    (byte)0x67, (byte)0x68, (byte)0x69, (byte)0x6a, (byte)0x73, (byte)0x74, (byte)0x75, (byte)0x76, (byte)0x77, (byte)0x78, (byte)0x79, (byte)0x7a,
    (byte)0x83, (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87, (byte)0x88, (byte)0x89, (byte)0x8a, (byte)0x92, (byte)0x93, (byte)0x94, (byte)0x95,
    (byte)0x96, (byte)0x97, (byte)0x98, (byte)0x99, (byte)0x9a, (byte)0xa2, (byte)0xa3, (byte)0xa4, (byte)0xa5, (byte)0xa6, (byte)0xa7, (byte)0xa8,
    (byte)0xa9, (byte)0xaa, (byte)0xb2, (byte)0xb3, (byte)0xb4, (byte)0xb5, (byte)0xb6, (byte)0xb7, (byte)0xb8, (byte)0xb9, (byte)0xba, (byte)0xc2,
    (byte)0xc3, (byte)0xc4, (byte)0xc5, (byte)0xc6, (byte)0xc7, (byte)0xc8, (byte)0xc9, (byte)0xca, (byte)0xd2, (byte)0xd3, (byte)0xd4, (byte)0xd5,
    (byte)0xd6, (byte)0xd7, (byte)0xd8, (byte)0xd9, (byte)0xda, (byte)0xe1, (byte)0xe2, (byte)0xe3, (byte)0xe4, (byte)0xe5, (byte)0xe6, (byte)0xe7,
    (byte)0xe8, (byte)0xe9, (byte)0xea, (byte)0xf1, (byte)0xf2, (byte)0xf3, (byte)0xf4, (byte)0xf5, (byte)0xf6, (byte)0xf7, (byte)0xf8, (byte)0xf9,
    (byte)0xfa, (byte)0x11, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x02, (byte)0x04, (byte)0x04, (byte)0x03, (byte)0x04, (byte)0x07, (byte)0x05,
    (byte)0x04, (byte)0x04, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x77, (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x11, (byte)0x04,
    (byte)0x05, (byte)0x21, (byte)0x31, (byte)0x06, (byte)0x12, (byte)0x41, (byte)0x51, (byte)0x07, (byte)0x61, (byte)0x71, (byte)0x13, (byte)0x22,
    (byte)0x32, (byte)0x81, (byte)0x08, (byte)0x14, (byte)0x42, (byte)0x91, (byte)0xa1, (byte)0xb1, (byte)0xc1, (byte)0x09, (byte)0x23, (byte)0x33,
    (byte)0x52, (byte)0xf0, (byte)0x15, (byte)0x62, (byte)0x72, (byte)0xd1, (byte)0x0a, (byte)0x16, (byte)0x24, (byte)0x34, (byte)0xe1, (byte)0x25,
    (byte)0xf1, (byte)0x17, (byte)0x18, (byte)0x19, (byte)0x1a, (byte)0x26, (byte)0x27, (byte)0x28, (byte)0x29, (byte)0x2a, (byte)0x35, (byte)0x36,
    (byte)0x37, (byte)0x38, (byte)0x39, (byte)0x3a, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47, (byte)0x48, (byte)0x49, (byte)0x4a,
    (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57, (byte)0x58, (byte)0x59, (byte)0x5a, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66,
    (byte)0x67, (byte)0x68, (byte)0x69, (byte)0x6a, (byte)0x73, (byte)0x74, (byte)0x75, (byte)0x76, (byte)0x77, (byte)0x78, (byte)0x79, (byte)0x7a,
    (byte)0x82, (byte)0x83, (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87, (byte)0x88, (byte)0x89, (byte)0x8a, (byte)0x92, (byte)0x93, (byte)0x94,
    (byte)0x95, (byte)0x96, (byte)0x97, (byte)0x98, (byte)0x99, (byte)0x9a, (byte)0xa2, (byte)0xa3, (byte)0xa4, (byte)0xa5, (byte)0xa6, (byte)0xa7,
    (byte)0xa8, (byte)0xa9, (byte)0xaa, (byte)0xb2, (byte)0xb3, (byte)0xb4, (byte)0xb5, (byte)0xb6, (byte)0xb7, (byte)0xb8, (byte)0xb9, (byte)0xba,
    (byte)0xc2, (byte)0xc3, (byte)0xc4, (byte)0xc5, (byte)0xc6, (byte)0xc7, (byte)0xc8, (byte)0xc9, (byte)0xca, (byte)0xd2, (byte)0xd3, (byte)0xd4,
    (byte)0xd5, (byte)0xd6, (byte)0xd7, (byte)0xd8, (byte)0xd9, (byte)0xda, (byte)0xe2, (byte)0xe3, (byte)0xe4, (byte)0xe5, (byte)0xe6, (byte)0xe7,
    (byte)0xe8, (byte)0xe9, (byte)0xea, (byte)0xf2, (byte)0xf3, (byte)0xf4, (byte)0xf5, (byte)0xf6, (byte)0xf7, (byte)0xf8, (byte)0xf9, (byte)0xfa
  };

  private static int clamp(int val) {
    return Math.max(0, Math.min(255, val));
  }

  public BufferedImage toBufferedImage() {
    switch (format) {
      case Uvc.UVC_FRAME_FORMAT_MJPEG:
        try {
          var hasDht = false;
          for (int i = 0; i < data.length - 1; i++) {
            if ((data[i] & 0xFF) == 0xFF && (data[i + 1] & 0xFF) == 0xC4) {
              hasDht = true;
              break;
            }
          }
          ByteArrayInputStream bis;
          if (!hasDht) {
            byte[] newData = new byte[data.length + DEFAULT_DHT.length];
            System.arraycopy(data, 0, newData, 0, 2);
            System.arraycopy(DEFAULT_DHT, 0, newData, 2, DEFAULT_DHT.length);
            System.arraycopy(data, 2, newData, 2 + DEFAULT_DHT.length, data.length - 2);
            bis = new ByteArrayInputStream(newData);
          } else {
            bis = new ByteArrayInputStream(data);
          }
          return ImageIO.read(bis);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      case Uvc.UVC_FRAME_FORMAT_YUYV:
        var yuyvImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        var yuyvRgb = new byte[width * height * 3];
        int yuyvIdx = 0;
        for (int i = 0; i < data.length; i += 4) {
          int y0 = data[i] & 0xFF, v = data[i + 1] & 0xFF;  // Swap U and V
          int y1 = data[i + 2] & 0xFF, u = data[i + 3] & 0xFF;  // Swap U and V
          int r0 = clamp(y0 + (int) (1.402 * (v - 128)));
          int g0 = clamp(y0 - (int) (0.344 * (u - 128)) - (int) (0.714 * (v - 128)));
          int b0 = clamp(y0 + (int) (1.772 * (u - 128)));
          int r1 = clamp(y1 + (int) (1.402 * (v - 128)));
          int g1 = clamp(y1 - (int) (0.344 * (u - 128)) - (int) (0.714 * (v - 128)));
          int b1 = clamp(y1 + (int) (1.772 * (u - 128)));
          yuyvRgb[yuyvIdx++] = (byte) b0; yuyvRgb[yuyvIdx++] = (byte) g0; yuyvRgb[yuyvIdx++] = (byte) r0;
          yuyvRgb[yuyvIdx++] = (byte) b1; yuyvRgb[yuyvIdx++] = (byte) g1; yuyvRgb[yuyvIdx++] = (byte) r1;
        }
        yuyvImg.getRaster().setDataElements(0, 0, width, height, yuyvRgb);
        return yuyvImg;
      case Uvc.UVC_FRAME_FORMAT_RGB:
        var rgbBgr = new byte[data.length];
        for (int i = 0; i < data.length; i += 3) {
          rgbBgr[i] = data[i + 2]; rgbBgr[i + 1] = data[i + 1]; rgbBgr[i + 2] = data[i];
        }
        var rgbImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        rgbImg.getRaster().setDataElements(0, 0, width, height, rgbBgr);
        return rgbImg;
      case Uvc.UVC_FRAME_FORMAT_BGR:
        var bgrImg = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        bgrImg.getRaster().setDataElements(0, 0, width, height, data);
        return bgrImg;
      case Uvc.UVC_FRAME_FORMAT_GRAY8:
        var grayImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        grayImg.getRaster().setDataElements(0, 0, width, height, data);
        return grayImg;
      default:
        throw new UnsupportedOperationException("Unsupported format: " + format);
    }
  }

}