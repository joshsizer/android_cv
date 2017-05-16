#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>
#include <GLES2/gl2.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_team341_daisycv_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jint JNICALL Java_com_team341_daisycv_MainActivity_matrixSize(
        JNIEnv *env,
        jobject) {

    int h = 480;
    int w = 640;

    static cv::Mat input;
    input.create(h, w, CV_8UC4);

    __android_log_print(ANDROID_LOG_DEBUG, "JNIPart", "Mat[][] is %d", input.at<cv::Vec3b>(400,400)[0]);

    // read
    glReadPixels(0, 0, w, h, GL_RGBA, GL_UNSIGNED_BYTE, input.data);

    __android_log_print(ANDROID_LOG_DEBUG, "JNIPart", "Mat[][] is %d", input.at<cv::Vec3b>(400,400)[0]);

    jint size = input.size().area();

    return size;
}
