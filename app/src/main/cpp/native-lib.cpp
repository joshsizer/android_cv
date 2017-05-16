#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <GLES2/gl2.h>
#include "include/common.hpp"

extern "C"
JNIEXPORT jstring JNICALL
Java_com_team341_daisycv_ImageProcessor_stringFromJNI(
    JNIEnv *env,
    jobject /* this */) {
  std::string hello = "Hello from C++";

  return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL Java_com_team341_daisycv_ImageProcessor_proccessImage(
    JNIEnv *env,
    jobject, int texIn, int texOut, int width, int height) {

  int64_t startTime;

  static cv::Mat input;
  input.create(height, width, CV_8UC4);
  LOGD("Got image of size %d x %d", input.cols, input.rows);

  startTime = getTimeMs();
  // read
  glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

  LOGD("glReadPixels took %d milliseconds", getTimeInterval(startTime));

  jint size = input.size().area();

  return size;
}
