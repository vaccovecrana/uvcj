package io.vacco.uvc;

public class UvcFrameDesc {

  public int width;
  public int height;
  public long minBitRate;
  public long maxBitRate;
  public long maxVideoFrameBufferSize;
  public long defaultFrameInterval;
  public long minFrameInterval;
  public long maxFrameInterval;
  public long frameIntervalStep;
  public long[] intervals;

  @Override public String toString() {
    return String.format(
      "%04dx%04d %012d/%012d / %010d",
      width, height,
      minBitRate, maxBitRate,
      defaultFrameInterval
    );
  }

}