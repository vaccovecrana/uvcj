# uvcj

JNI wrapper for `libuvc`.

Requires `libuvc` available at `LD_LIBRARY_PATH`. 

Grant access to USB devices at `/etc/udev/rules.d/90-usbpermission.rules`:

```
SUBSYSTEM=="usb",GROUP="plugdev",MODE="0666"
```

---

To use `zig` for c/c++ compilation:

```
sudo update-alternatives --install /usr/bin/g++ cc ~/Applications/zig-linux-x86_64-0.10.1/zig 50
sudo update-alternatives --query cc
sudo update-alternatives --query cc
sudo update-alternatives --remove cc ~/Applications/zig-linux-x86_64-0.10.1/cc
```
