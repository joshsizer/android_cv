#include <jni.h>
#include <string>
#include "opencv2/core.hpp"

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_joshs_daisycv_MainActivity_stringJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";

    static cv::Mat m;
    //m.create(4, 5, CV_8UC4);

    return env->NewStringUTF(hello.c_str());
}
