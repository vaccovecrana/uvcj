package io.vacco.uvc;

import java.util.ArrayList;
import java.util.List;

public class UvcFormatDesc {

  public int subtype;
  public String fourccFormat;
  public byte[] guidFormat;
  public int formatSpecific;
  public List<UvcFrameDesc> frameDescs = new ArrayList<>();

  @Override public String toString() {
    return String.format("%02d/%s: %s", subtype, fourccFormat, frameDescs);
  }

}
