CC=zig cc
UNAME_S := $(shell uname -s)

JAVA_INCLUDE := $(shell java -XshowSettings:properties -version 2>&1 | grep 'java.home' | awk '{print $$3 "/include"}')

ifeq ($(UNAME_S),Linux)
    PLATFORM=linux
    SOEXT=so
    SOFLAGS=-shared -fPIC
    CFLAGS=-Ilinux-amd64/include -Ilinux-amd64/include/libuvc
    STATIC_LIBS=linux-amd64/lib/libuvc.a linux-amd64/lib/libusb-1.0.a linux-amd64/lib/libjpeg.a
    LDFLAGS=-Wl,--whole-archive $(STATIC_LIBS) -Wl,--no-whole-archive -lpthread
endif
ifeq ($(UNAME_S),Darwin)
    PLATFORM=darwin
    SOEXT=dylib
    SOFLAGS=-dynamiclib
    CFLAGS=-Idarwin-amd64/include -Idarwin-amd64/include/libuvc
    STATIC_LIBS=darwin-amd64/lib/libuvc.a darwin-amd64/lib/libusb-1.0.a darwin-amd64/lib/libjpeg.a
    LDFLAGS=-all_load $(STATIC_LIBS) -lpthread -framework CoreFoundation -framework IOKit -framework Security
endif

JNI_CFLAGS=$(CFLAGS) -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE)/$(PLATFORM)

TARGET=uvc_test
JNI_LIB=libuvc_jni.$(SOEXT)

all: $(TARGET) $(JNI_LIB)

$(TARGET): test/main.c
	$(CC) $(CFLAGS) $(LDFLAGS) -o $@ $<

$(JNI_LIB): jni/uvc.c
	$(CC) $(JNI_CFLAGS) $(SOFLAGS) $(LDFLAGS) -o $@ $<

clean:
	rm -f $(TARGET) $(JNI_LIB)