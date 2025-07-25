package io.vacco.uvc;

public interface UvcFrameCallback {

  void onFrame(UvcFrame frame, Object userData);

}
