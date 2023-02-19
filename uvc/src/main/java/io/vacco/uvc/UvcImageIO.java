package io.vacco.uvc;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.nio.ByteBuffer;

public class UvcImageIO {

  public static void copyByteBuffer(ByteBuffer srcBuf, int srcStep, ByteBuffer dstBuf, int dstStep) {
    int channels = 3;
    int w = Math.min(srcStep, dstStep);
    int srcLine = srcBuf.position(), dstLine = dstBuf.position();
    var buffer = new byte[channels];

    while (srcLine < srcBuf.capacity() && dstLine < dstBuf.capacity()) {
      srcBuf.position(srcLine);
      dstBuf.position(dstLine);
      w = Math.min(Math.min(w, srcBuf.remaining()), dstBuf.remaining());
      for (int x = 0; x < w; x += channels) {
        for (int z = 0; z < channels; z++) {
          int in = srcBuf.get();
          byte out;
          out = (byte) in;
          buffer[z] = out;
        }
        for (int z = channels - 1; z >= 0; z--) {
          dstBuf.put(buffer[z]);
        }
      }
      srcLine += srcStep;
      dstLine += dstStep;
    }
  }

  public static BufferedImage imageFrom(Uvc.UVCFrame frame) {
    var cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    var offsets = new int[]{2, 1, 0}; // raster in "BGR" order like OpenCV..
    var cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
    var csm = new ComponentSampleModel(DataBuffer.TYPE_BYTE, frame.width(), frame.height(), 3, frame.width() * 3, offsets);
    var wr = Raster.createWritableRaster(csm, null);

    var img = new BufferedImage(cm, wr, false, null);
    var out = (DataBufferByte) img.getRaster().getDataBuffer();
    var buff = frame.data().capacity(frame.data_bytes()).asByteBuffer();

    copyByteBuffer(buff, frame.width() * 3, ByteBuffer.wrap(out.getData()), frame.width() * 3);

    return img;
  }

}
