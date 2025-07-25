#include <stdio.h>
#include <unistd.h>
#include <libuvc/libuvc.h>

void cb(uvc_frame_t *frame, void *ptr) {
  printf("Frame received: %d x %d, format %d, size %zu\n",
         (int)frame->width, (int)frame->height,
         frame->frame_format, frame->data_bytes);
  *(int*)ptr = 1;
}

int main() {
  uvc_context_t *ctx;
  uvc_error_t res = uvc_init(&ctx, NULL);
  if (res < 0) {
    uvc_perror(res, "uvc_init");
    return 1;
  }
  printf("UVC initialized\n");

  uvc_device_t **devs;
  res = uvc_get_device_list(ctx, &devs);
  if (res < 0) {
    uvc_perror(res, "uvc_get_device_list");
    uvc_exit(ctx);
    return 1;
  }

  int num_cameras = 0;
  uvc_device_t *first_camera = NULL;
  int i = 0;
  while (devs[i] != NULL) {
    uvc_device_descriptor_t *desc;
    res = uvc_get_device_descriptor(devs[i], &desc);
    if (res == 0) {
      printf("Camera %d: %s %s (serial %s)\n", num_cameras,
             desc->manufacturer ? desc->manufacturer : "?",
             desc->product ? desc->product : "?",
             desc->serialNumber ? desc->serialNumber : "?");
      uvc_free_device_descriptor(desc);
      if (num_cameras == 0) first_camera = devs[i];
      num_cameras++;
    }
    i++;
  }

  if (num_cameras == 0) {
    printf("No cameras found\n");
    uvc_free_device_list(devs, 1);
    uvc_exit(ctx);
    return 1;
  }

  uvc_device_handle_t *devh;
  res = uvc_open(first_camera, &devh);
  if (res < 0) {
    uvc_perror(res, "uvc_open");
    uvc_free_device_list(devs, 1);
    uvc_exit(ctx);
    return 1;
  }
  printf("Opened first camera\n");

  uvc_stream_ctrl_t ctrl;
  res = uvc_get_stream_ctrl_format_size(devh, &ctrl, UVC_FRAME_FORMAT_MJPEG, 640, 480, 30);
  if (res < 0) {
    res = uvc_get_stream_ctrl_format_size(devh, &ctrl, UVC_FRAME_FORMAT_YUYV, 640, 480, 30);
    if (res < 0) {
      uvc_perror(res, "uvc_get_stream_ctrl_format_size");
      uvc_close(devh);
      uvc_free_device_list(devs, 1);
      uvc_exit(ctx);
      return 1;
    }
  }

  int got_frame = 0;
  res = uvc_start_streaming(devh, &ctrl, cb, &got_frame, 0);
  if (res < 0) {
    uvc_perror(res, "uvc_start_streaming");
  } else {
    while (!got_frame) {
      usleep(10000);
    }
    uvc_stop_streaming(devh);
    printf("Got frame\n");
  }

  uvc_close(devh);
  uvc_free_device_list(devs, 1);
  uvc_exit(ctx);
  return 0;
}