# uvcj

JNI wrapper for `libuvc`.

Available in [Maven Central](https://mvnrepository.com/artifact/io.vacco.uvcj/uvc).

Supports Linux/MacOS on x64 devices.

Includes Java classes to control a camera, and extract `BufferedImage` frames from it.

See [examples](./src/test/java/io/vacco/uvc/UvcTest.java) for usage.

MacOS support is secondary, since their permissions model makes testing very painful.

## Notes

To grant access to USB devices, create a `udev` rule at `/etc/udev/rules.d/90-usbpermission.rules`.

```
SUBSYSTEM=="usb",GROUP="plugdev",MODE="0666"
```
