#include <jni.h>

#ifndef _Included_io_vacco_uvc_Uvc
#define _Included_io_vacco_uvc_Uvc
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_initContext
  (JNIEnv *, jclass);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_exitContext
  (JNIEnv *, jclass, jlong);

JNIEXPORT jlongArray JNICALL Java_io_vacco_uvc_Uvc_getDeviceList
  (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_freeDeviceList
  (JNIEnv *, jclass, jlongArray);

JNIEXPORT jobject JNICALL Java_io_vacco_uvc_Uvc_getDeviceDescriptor
  (JNIEnv *, jclass, jlong);

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_openDevice
  (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_closeDevice
  (JNIEnv *, jclass, jlong);

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_getStreamCtrlFormatSize
  (JNIEnv *, jclass, jlong, jint, jint, jint, jint);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_startStreaming
  (JNIEnv *, jclass, jlong, jlong, jobject, jobject, jint);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_stopStreaming
  (JNIEnv *, jclass, jlong);

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_setFocusAuto
  (JNIEnv *, jclass, jlong, jboolean);

JNIEXPORT jobjectArray JNICALL Java_io_vacco_uvc_Uvc_getFormatDescriptors
  (JNIEnv *, jclass, jlong);

#ifdef __cplusplus
}
#endif
#endif