#include <jni.h>
#include <libuvc/libuvc.h>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include "uvc.h"

typedef struct {
  JavaVM *vm;
  jobject callback;
  jobject userData;
  jclass frameClass;
  jmethodID frameCtor;
  jfieldID widthField;
  jfieldID heightField;
  jfieldID formatField;
  jfieldID dataField;
  jmethodID onFrameMethod;
} CallbackData;

static CallbackData *globalCallbackData = NULL;

static void throwUvcException(JNIEnv *env, uvc_error_t res, const char *msg) {
  if (res >= 0) return;
  char buf[256];
  snprintf(buf, sizeof(buf), "%s: %s (%d)", msg, uvc_strerror(res), res);
  jclass excClass = (*env)->FindClass(env, "java/lang/RuntimeException");
  (*env)->ThrowNew(env, excClass, buf);
}

static void wrapped_cb(uvc_frame_t *frame, void *ptr) {
  CallbackData *data = (CallbackData *) ptr;
  JNIEnv *env = NULL;
  jint attachStatus = (*data->vm)->AttachCurrentThread(data->vm, (void **) &env, NULL);
  if (attachStatus != JNI_OK) {
    return;
  }

  jobject jFrame = (*env)->NewObject(env, data->frameClass, data->frameCtor);
  (*env)->SetIntField(env, jFrame, data->widthField, (jint) frame->width);
  (*env)->SetIntField(env, jFrame, data->heightField, (jint) frame->height);
  (*env)->SetIntField(env, jFrame, data->formatField, (jint) frame->frame_format);

  jbyteArray jData = (*env)->NewByteArray(env, (jsize) frame->data_bytes);
  (*env)->SetByteArrayRegion(env, jData, 0, (jsize) frame->data_bytes, (jbyte *) frame->data);
  (*env)->SetObjectField(env, jFrame, data->dataField, jData);

  (*env)->CallVoidMethod(env, data->callback, data->onFrameMethod, jFrame, data->userData);

  if (attachStatus == JNI_OK) {
    (*data->vm)->DetachCurrentThread(data->vm);
  }
}

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_initContext(JNIEnv *env, jclass cls) {
  uvc_context_t *ctx;
  uvc_error_t res = uvc_init(&ctx, NULL);
  throwUvcException(env, res, "uvc_init");
  return (jlong) ctx;
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_exitContext(JNIEnv *env, jclass cls, jlong ctx) {
  uvc_exit((uvc_context_t *) ctx);
}

JNIEXPORT jlongArray JNICALL Java_io_vacco_uvc_Uvc_getDeviceList(JNIEnv *env, jclass cls, jlong ctx) {
  uvc_device_t **devs;
  uvc_error_t res = uvc_get_device_list((uvc_context_t *) ctx, &devs);
  throwUvcException(env, res, "uvc_get_device_list");
  int count = 0;
  while (devs[count] != NULL) count++;
  jlongArray arr = (*env)->NewLongArray(env, count);
  jlong *elems = malloc(count * sizeof(jlong));
  for (int i = 0; i < count; i++) {
    elems[i] = (jlong) devs[i];
  }
  (*env)->SetLongArrayRegion(env, arr, 0, count, elems);
  free(elems);
  uvc_free_device_list(devs, 0);  // Free the list but not the devices yet
  return arr;
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_freeDeviceList(JNIEnv *env, jclass cls, jlongArray devsArr) {
  jsize len = (*env)->GetArrayLength(env, devsArr);
  jlong *devs = (*env)->GetLongArrayElements(env, devsArr, NULL);
  uvc_device_t **list = malloc((len + 1) * sizeof(uvc_device_t *));
  for (int i = 0; i < len; i++) {
    list[i] = (uvc_device_t *) devs[i];
  }
  list[len] = NULL;
  uvc_free_device_list(list, 1);
  (*env)->ReleaseLongArrayElements(env, devsArr, devs, 0);
}

JNIEXPORT jobject JNICALL Java_io_vacco_uvc_Uvc_getDeviceDescriptor(JNIEnv *env, jclass cls, jlong dev) {
  uvc_device_descriptor_t *desc;
  uvc_error_t res = uvc_get_device_descriptor((uvc_device_t *) dev, &desc);
  throwUvcException(env, res, "uvc_get_device_descriptor");
  jclass descClass = (*env)->FindClass(env, "io/vacco/uvc/UvcDeviceDescriptor");
  jmethodID ctor = (*env)->GetMethodID(env, descClass, "<init>", "()V");
  jobject jDesc = (*env)->NewObject(env, descClass, ctor);
  jfieldID manField = (*env)->GetFieldID(env, descClass, "manufacturer", "Ljava/lang/String;");
  jfieldID prodField = (*env)->GetFieldID(env, descClass, "product", "Ljava/lang/String;");
  jfieldID serField = (*env)->GetFieldID(env, descClass, "serialNumber", "Ljava/lang/String;");
  (*env)->SetObjectField(env, jDesc, manField, (*env)->NewStringUTF(env, desc->manufacturer ? desc->manufacturer : "?"));
  (*env)->SetObjectField(env, jDesc, prodField, (*env)->NewStringUTF(env, desc->product ? desc->product : "?"));
  (*env)->SetObjectField(env, jDesc, serField, (*env)->NewStringUTF(env, desc->serialNumber ? desc->serialNumber : "?"));
  uvc_free_device_descriptor(desc);
  return jDesc;
}

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_openDevice(JNIEnv *env, jclass cls, jlong dev) {
  uvc_device_handle_t *devh;
  uvc_error_t res = uvc_open((uvc_device_t *) dev, &devh);
  throwUvcException(env, res, "uvc_open");
  return (jlong) devh;
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_closeDevice(JNIEnv *env, jclass cls, jlong devh) {
  uvc_close((uvc_device_handle_t *) devh);
}

JNIEXPORT jlong JNICALL Java_io_vacco_uvc_Uvc_getStreamCtrlFormatSize(JNIEnv *env, jclass cls, jlong devh, jint format, jint width, jint height, jint fps) {
  uvc_stream_ctrl_t *ctrl = malloc(sizeof(uvc_stream_ctrl_t));
  if (ctrl == NULL) {
    throwUvcException(env, UVC_ERROR_NO_MEM, "malloc");
    return 0;
  }
  uvc_error_t res = uvc_get_stream_ctrl_format_size((uvc_device_handle_t *) devh, ctrl, (enum uvc_frame_format) format, width, height, fps);
  if (res < 0) {
    free(ctrl);
    throwUvcException(env, res, "uvc_get_stream_ctrl_format_size");
    return 0;
  }
  return (jlong) ctrl;
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_startStreaming(JNIEnv *env, jclass cls, jlong devh, jlong ctrl, jobject callback, jobject userData, jint flags) {
  if (globalCallbackData != NULL) {
    throwUvcException(env, UVC_ERROR_BUSY, "Streaming already in progress");
    return;
  }

  jclass frameCls = (*env)->FindClass(env, "io/vacco/uvc/UvcFrame");
  jmethodID frameCtor = (*env)->GetMethodID(env, frameCls, "<init>", "()V");
  jfieldID widthF = (*env)->GetFieldID(env, frameCls, "width", "I");
  jfieldID heightF = (*env)->GetFieldID(env, frameCls, "height", "I");
  jfieldID formatF = (*env)->GetFieldID(env, frameCls, "format", "I");
  jfieldID dataF = (*env)->GetFieldID(env, frameCls, "data", "[B");

  jclass cbCls = (*env)->FindClass(env, "io/vacco/uvc/UvcFrameCallback");
  jmethodID onFrameM = (*env)->GetMethodID(env, cbCls, "onFrame", "(Lio/vacco/uvc/UvcFrame;Ljava/lang/Object;)V");

  globalCallbackData = malloc(sizeof(CallbackData));
  if (globalCallbackData == NULL) {
    throwUvcException(env, UVC_ERROR_NO_MEM, "malloc");
    return;
  }

  (*env)->GetJavaVM(env, &globalCallbackData->vm);
  globalCallbackData->callback = (*env)->NewGlobalRef(env, callback);
  globalCallbackData->userData = (*env)->NewGlobalRef(env, userData);
  globalCallbackData->frameClass = (jclass) (*env)->NewGlobalRef(env, (jobject) frameCls);
  globalCallbackData->frameCtor = frameCtor;
  globalCallbackData->widthField = widthF;
  globalCallbackData->heightField = heightF;
  globalCallbackData->formatField = formatF;
  globalCallbackData->dataField = dataF;
  globalCallbackData->onFrameMethod = onFrameM;

  uvc_error_t res = uvc_start_streaming((uvc_device_handle_t *) devh, (uvc_stream_ctrl_t *) ctrl, wrapped_cb, globalCallbackData, (uint8_t) flags);
  free((void *) ctrl);

  if (res < 0) { // Cleanup on failure
    (*env)->DeleteGlobalRef(env, globalCallbackData->callback);
    (*env)->DeleteGlobalRef(env, globalCallbackData->userData);
    (*env)->DeleteGlobalRef(env, (jobject) globalCallbackData->frameClass);
    free(globalCallbackData);
    globalCallbackData = NULL;
    throwUvcException(env, res, "uvc_start_streaming");
  }
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_stopStreaming(JNIEnv *env, jclass cls, jlong devh) {
  uvc_stop_streaming((uvc_device_handle_t *) devh);
  if (globalCallbackData != NULL) {
    (*env)->DeleteGlobalRef(env, globalCallbackData->callback);
    (*env)->DeleteGlobalRef(env, globalCallbackData->userData);
    (*env)->DeleteGlobalRef(env, (jobject) globalCallbackData->frameClass);
    free(globalCallbackData);
    globalCallbackData = NULL;
  }
}

JNIEXPORT void JNICALL Java_io_vacco_uvc_Uvc_setFocusAuto(JNIEnv *env, jclass cls, jlong devh, jboolean autoFocus) {
  uvc_error_t res = uvc_set_focus_auto((uvc_device_handle_t *) devh, (uint8_t) (autoFocus ? 1 : 0));
  throwUvcException(env, res, "uvc_set_focus_auto");
}

JNIEXPORT jobjectArray JNICALL Java_io_vacco_uvc_Uvc_getFormatDescriptors(JNIEnv *env, jclass cls, jlong devh) {
  uvc_format_desc_t *formats = uvc_get_format_descs((uvc_device_handle_t *) devh);
  int count = 0;
  uvc_format_desc_t *fmt;
  for (fmt = formats; fmt != NULL; fmt = fmt->next) {
    count++;
  }

  jclass fmtClass = (*env)->FindClass(env, "io/vacco/uvc/UvcFormatDesc");
  jobjectArray arr = (*env)->NewObjectArray(env, count, fmtClass, NULL);

  jmethodID fmtCtor = (*env)->GetMethodID(env, fmtClass, "<init>", "()V");
  jfieldID subtypeField = (*env)->GetFieldID(env, fmtClass, "subtype", "I");
  jfieldID fourccField = (*env)->GetFieldID(env, fmtClass, "fourccFormat", "Ljava/lang/String;");
  jfieldID guidField = (*env)->GetFieldID(env, fmtClass, "guidFormat", "[B");
  jfieldID fmtSpecField = (*env)->GetFieldID(env, fmtClass, "formatSpecific", "I");
  jfieldID framesField = (*env)->GetFieldID(env, fmtClass, "frameDescs", "Ljava/util/List;");

  jclass listClass = (*env)->FindClass(env, "java/util/List");
  jmethodID addMethod = (*env)->GetMethodID(env, listClass, "add", "(Ljava/lang/Object;)Z");

  jclass frameClass = (*env)->FindClass(env, "io/vacco/uvc/UvcFrameDesc");
  jmethodID frameCtor = (*env)->GetMethodID(env, frameClass, "<init>", "()V");
  jfieldID widthField = (*env)->GetFieldID(env, frameClass, "width", "I");
  jfieldID heightField = (*env)->GetFieldID(env, frameClass, "height", "I");
  jfieldID minBrField = (*env)->GetFieldID(env, frameClass, "minBitRate", "J");
  jfieldID maxBrField = (*env)->GetFieldID(env, frameClass, "maxBitRate", "J");
  jfieldID maxFrameSizeField = (*env)->GetFieldID(env, frameClass, "maxVideoFrameBufferSize", "J");
  jfieldID defIntField = (*env)->GetFieldID(env, frameClass, "defaultFrameInterval", "J");
  jfieldID minIntField = (*env)->GetFieldID(env, frameClass, "minFrameInterval", "J");
  jfieldID maxIntField = (*env)->GetFieldID(env, frameClass, "maxFrameInterval", "J");
  jfieldID stepIntField = (*env)->GetFieldID(env, frameClass, "frameIntervalStep", "J");
  jfieldID intervalsField = (*env)->GetFieldID(env, frameClass, "intervals", "[J");

  int i = 0;
  for (fmt = formats; fmt != NULL; fmt = fmt->next, i++) {
    jobject jFmt = (*env)->NewObject(env, fmtClass, fmtCtor);
    (*env)->SetIntField(env, jFmt, subtypeField, (jint) fmt->bDescriptorSubtype);
    (*env)->SetIntField(env, jFmt, fmtSpecField, (jint) fmt->bBitsPerPixel);  // Union, works for all

    if (fmt->bDescriptorSubtype == UVC_VS_FORMAT_UNCOMPRESSED || fmt->bDescriptorSubtype == UVC_VS_FORMAT_MJPEG) {
      char fourcc[5];
      memcpy(fourcc, fmt->fourccFormat, 4);
      fourcc[4] = '\0';
      (*env)->SetObjectField(env, jFmt, fourccField, (*env)->NewStringUTF(env, fourcc));
    } else if (fmt->bDescriptorSubtype == UVC_VS_FORMAT_FRAME_BASED) {
      jbyteArray guidArr = (*env)->NewByteArray(env, 16);
      (*env)->SetByteArrayRegion(env, guidArr, 0, 16, (jbyte *) fmt->guidFormat);
      (*env)->SetObjectField(env, jFmt, guidField, guidArr);
    }

    jobject jFrames = (*env)->GetObjectField(env, jFmt, framesField);

    uvc_frame_desc_t *frame_desc = fmt->frame_descs;
    while (frame_desc) {
      jobject jFrameDesc = (*env)->NewObject(env, frameClass, frameCtor);
      (*env)->SetIntField(env, jFrameDesc, widthField, (jint) frame_desc->wWidth);
      (*env)->SetIntField(env, jFrameDesc, heightField, (jint) frame_desc->wHeight);
      (*env)->SetLongField(env, jFrameDesc, minBrField, (jlong) frame_desc->dwMinBitRate);
      (*env)->SetLongField(env, jFrameDesc, maxBrField, (jlong) frame_desc->dwMaxBitRate);
      (*env)->SetLongField(env, jFrameDesc, maxFrameSizeField, (jlong) frame_desc->dwMaxVideoFrameBufferSize);
      (*env)->SetLongField(env, jFrameDesc, defIntField, (jlong) frame_desc->dwDefaultFrameInterval);

      if (frame_desc->bFrameIntervalType == 0) {
        (*env)->SetLongField(env, jFrameDesc, minIntField, (jlong) frame_desc->dwMinFrameInterval);
        (*env)->SetLongField(env, jFrameDesc, maxIntField, (jlong) frame_desc->dwMaxFrameInterval);
        (*env)->SetLongField(env, jFrameDesc, stepIntField, (jlong) frame_desc->dwFrameIntervalStep);
      } else {
        jlongArray intArr = (*env)->NewLongArray(env, frame_desc->bFrameIntervalType);
        jlong *ints = malloc(frame_desc->bFrameIntervalType * sizeof(jlong));
        for (uint8_t k = 0; k < frame_desc->bFrameIntervalType; k++) {
          ints[k] = (jlong) frame_desc->intervals[k];
        }
        (*env)->SetLongArrayRegion(env, intArr, 0, frame_desc->bFrameIntervalType, ints);
        free(ints);
        (*env)->SetObjectField(env, jFrameDesc, intervalsField, intArr);
      }

      (*env)->CallBooleanMethod(env, jFrames, addMethod, jFrameDesc);
      frame_desc = frame_desc->next;
    }

    (*env)->SetObjectArrayElement(env, arr, i, jFmt);
  }

  return arr;
}
