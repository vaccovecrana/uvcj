#!/bin/bash

# Script to install prerequisites, download and build libusb, libjpeg-turbo, and libuvc as static libraries with -fPIC,
# This script assumes it's run from the project root (e.g., src/main/c).
# It uses /tmp for builds and cleans up afterward.

set -e

# Define output directories
OUTPUT_DIR="$(pwd)/$(uname -s | tr '[:upper:]' '[:lower:]')-amd64"
mkdir -p "$OUTPUT_DIR/lib" "$OUTPUT_DIR/include" "$OUTPUT_DIR/include/libuvc"

# Step 1: Install necessary apt packages
# echo "Installing prerequisites via apt..."
# sudo apt update
# sudo apt install -y build-essential git cmake autoconf automake libtool pkg-config

# Step 2: Build libusb (static, -fPIC, disable-udev)
echo "Building libusb..."
LIBUSB_BUILD_DIR="/tmp/libusb-build"
rm -rf "$LIBUSB_BUILD_DIR"
mkdir -p "$LIBUSB_BUILD_DIR"
cd "$LIBUSB_BUILD_DIR"
git clone https://github.com/libusb/libusb.git
cd libusb
./autogen.sh
./configure --enable-static --disable-shared --disable-udev CFLAGS="-fPIC" --prefix="$LIBUSB_BUILD_DIR/install"
make -j$(nproc)
make install
# Copy artifacts
cp "$LIBUSB_BUILD_DIR/install/lib/libusb-1.0.a" "$OUTPUT_DIR/lib/"
cp -r "$LIBUSB_BUILD_DIR/install/include/libusb-1.0" "$OUTPUT_DIR/include/"

# Step 3: Build libjpeg-turbo (static, -fPIC)
echo "Building libjpeg-turbo..."
JPEG_BUILD_DIR="/tmp/libjpeg-build"
rm -rf "$JPEG_BUILD_DIR"
mkdir -p "$JPEG_BUILD_DIR"
cd "$JPEG_BUILD_DIR"
git clone https://github.com/libjpeg-turbo/libjpeg-turbo.git
cd libjpeg-turbo
mkdir build && cd build
cmake .. -DCMAKE_INSTALL_PREFIX="../install" -DCMAKE_POSITION_INDEPENDENT_CODE=ON -DENABLE_SHARED=OFF
make -j$(nproc)
make install
# Copy artifacts
cp "../install/lib/libjpeg.a" "$OUTPUT_DIR/lib/"
cp "../install/include/"*.h "$OUTPUT_DIR/include/"

# Step 4: Build libuvc (static, -fPIC, using built libusb and libjpeg)
echo "Building libuvc..."
UVC_BUILD_DIR="/tmp/libuvc-build"
rm -rf "$UVC_BUILD_DIR"
mkdir -p "$UVC_BUILD_DIR"
cd "$UVC_BUILD_DIR"
git clone https://github.com/libuvc/libuvc.git
cd libuvc
mkdir build && cd build
cmake .. -DBUILD_SHARED_LIBS=OFF -DCMAKE_POSITION_INDEPENDENT_CODE=ON -DCMAKE_INSTALL_PREFIX="../install" \
  -DLIBUSB_LIBRARIES="$LIBUSB_BUILD_DIR/install/lib/libusb-1.0.a" \
  -DLIBUSB_INCLUDE_DIR="$LIBUSB_BUILD_DIR/install/include" \
  -DJPEG_LIBRARY="$JPEG_BUILD_DIR/libjpeg-turbo/install/lib/libjpeg.a" \
  -DJPEG_INCLUDE_DIR="$JPEG_BUILD_DIR/libjpeg-turbo/install/include" \
  -DCMAKE_POLICY_VERSION_MINIMUM=3.5
make -j$(nproc)
make install
# Copy artifacts
cp "../install/lib/libuvc.a" "$OUTPUT_DIR/lib/"
cp -r "../install/include/libuvc"/* "$OUTPUT_DIR/include/libuvc/"

# Cleanup build directories
echo "Cleaning up temporary build directories..."
rm -rf "$LIBUSB_BUILD_DIR" "$JPEG_BUILD_DIR" "$UVC_BUILD_DIR"

echo "Build complete! Static libraries and headers are in $OUTPUT_DIR:"
ls -l "$OUTPUT_DIR/lib/"*.a
ls -lR "$OUTPUT_DIR/include"

echo "You can now run 'make clean && make' to build"
