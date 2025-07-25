## MacOS

My God, MacOS camera permissions are PAIN...

```
java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $3 "/bin/java"}'

sqlite3 ~/Library/Application\ Support/com.apple.TCC/TCC.db "DELETE FROM access WHERE client = '/Users/jjzazuet/Applications/zulu11.80.21-ca-jdk11.0.27-macosx_x64/bin/java' AND service = 'kTCCServiceCamera';"

sqlite3 ~/Library/Application\ Support/com.apple.TCC/TCC.db "INSERT OR REPLACE INTO access (service, client, client_type, auth_value, auth_reason, auth_version, csreq, policy_id, indirect_object_identifier_type, indirect_object_identifier, indirect_object_code_identity, flags, last_modified) VALUES ('kTCCServiceCamera', '/Users/jjzazuet/Applications/zulu11.80.21-ca-jdk11.0.27-macosx_x64/bin/java', 1, 2, 2, 1, NULL, NULL, NULL, 'UNUSED', NULL, 0, strftime('%s','now'));"
sqlite3 ~/Library/Application\ Support/com.apple.TCC/TCC.db "INSERT OR REPLACE INTO access (service, client, client_type, auth_value, auth_reason, auth_version, csreq, policy_id, indirect_object_identifier_type, indirect_object_identifier, indirect_object_code_identity, flags, last_modified) VALUES ('kTCCServiceCamera', '/Users/jjzazuet/Applications/IntelliJ IDEA CE.app/Contents/MacOS/idea', 1, 2, 2, 1, NULL, NULL, NULL, 'UNUSED', NULL, 0, strftime('%s','now'));"
sqlite3 ~/Library/Application\ Support/com.apple.TCC/TCC.db "INSERT OR REPLACE INTO access (service, client, client_type, auth_value, auth_reason, auth_version, csreq, policy_id, indirect_object_identifier_type, indirect_object_identifier, indirect_object_code_identity, flags, last_modified) VALUES ('kTCCServiceCamera', '/Users/jjzazuet/Applications/gradle-8.13/bin/gradle', 1, 2, 2, 1, NULL, NULL, NULL, 'UNUSED', NULL, 0, strftime('%s','now'));"
```

Verify:

```
sqlite3 ~/Library/Application\ Support/com.apple.TCC/TCC.db "SELECT * FROM access WHERE client = '/Users/jjzazuet/Applications/zulu11.80.21-ca-jdk11.0.27-macosx_x64/bin/java';"
```

## Debian

### Build libusb from source

```
mkdir -p /tmp/libusb-build && cd /tmp/libusb-build
git clone https://github.com/libusb/libusb.git
cd libusb
./autogen.sh
./configure --enable-static --disable-shared --disable-udev CFLAGS="-fPIC" --prefix=$(pwd)/install
make -j$(nproc)
make install
```

Move the `.a` and `.h` files to the `Linux` C source directory.

```
apt install gdb
/sbin/usermod -aG video $USER
```

```
lsusb

Bus 001 Device 010: ID 045e:0779 Microsoft Corp. LifeCam HD-3000
```

Edit /etc/udev/rules.d/99-uvc.rules``

```
SUBSYSTEM=="usb", ATTR{idVendor}=="045e", ATTR{idProduct}=="0779", MODE="0666", GROUP="plugdev"
```

Reload

```
udevadm control --reload-rules && udevadm trigger
```

```
LD_LIBRARY_PATH=/home/jjzazuet/code/infra/uvcj/src/main/c:/home/jjzazuet/code/uvcj/src/main/c/linux-amd64/lib \
java -cp src/main/java:src/test/java io.vacco.uvc.UvcTest
```