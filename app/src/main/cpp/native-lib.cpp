#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <GLES2/gl2.h>
#include "common.hpp"

extern "C" void process(int texIn, int texOut, int width, int height) {
  bool debug_mode = false;
  int64_t startTime;

  static cv::Mat input;
  input.create(height, width, CV_8UC4);

  if (debug_mode) LOGD("Got image of size %d x %d", input.cols, input.rows);

  startTime = getTimeMs();
  // read
  glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

  if (debug_mode) LOGD("glReadPixels took %d milliseconds", getTimeInterval(startTime));

  cv::Mat vis = input;
  cv::circle(vis, cv::Point(width/2, height/2), 100,
             cv::Scalar(0, 112, 255), 3);
  glActiveTexture(GL_TEXTURE0);
  glBindTexture(GL_TEXTURE_2D, texOut);
  glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE,
                  vis.data);
}

extern "C" JNIEXPORT void JNICALL Java_com_team341_daisycv_ImageProcessor_processImage(
    JNIEnv *env,
    jobject, jint texIn, jint texOut, jint width, jint height) {

  process(texIn, texOut, width, height);
}
