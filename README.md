# uvcj

JNI wrapper for `libuvc`.

Requires `libuvc` available at `LD_LIBRARY_PATH`. 

Includes Java classes to control a camera, and extract `BufferedImage` frames from it.

## Notes

To grant access to USB devices, create a `udev` rule at `/etc/udev/rules.d/90-usbpermission.rules`.

```
SUBSYSTEM=="usb",GROUP="plugdev",MODE="0666"
```
