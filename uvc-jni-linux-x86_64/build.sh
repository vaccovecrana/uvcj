~/Applications/zig-linux-x86_64-0.10.1/g++ \
  -I/usr/local/include \
  -I/home/jjzazuet/Applications/zulu11.52.13-ca-jdk11.0.13-linux_x64/include \
  -I/home/jjzazuet/Applications/zulu11.52.13-ca-jdk11.0.13-linux_x64/include/linux \
  /home/jjzazuet/code/uvcj/uvc-jni/build/javacpp/jniUVCController.cpp \
  /home/jjzazuet/code/uvcj/uvc-jni/build/javacpp/jnijavacpp.cpp \
  -march=x86_64 -m64 -O3 -s -luvc \
  -Wl,-rpath,$ORIGIN/ -Wl,-z,noexecstack -Wl,-Bsymbolic -Wall -fPIC -pthread \
  -static -o libjniUVCController.a \
  -L/usr/local/lib \
  -Wl,-rpath,/usr/local/lib -luvc
