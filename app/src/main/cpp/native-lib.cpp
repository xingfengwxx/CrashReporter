#include <jni.h>
#include "log.h"
#include "client/linux/handler/minidump_descriptor.h"
#include "client/linux/handler/exception_handler.h"

bool DumpCallback(const google_breakpad::MinidumpDescriptor& descriptor,
                  void* context,
                  bool succeeded) {
    LOGD("Dump path: %s\n", descriptor.path());
    // 如果回调返回true，Breakpad将把异常视为已完全处理，禁止任何其他处理程序收到异常通知。
    // 如果回调返回false，Breakpad会将异常视为未处理，并允许其他处理程序处理它。
    return false;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wangxingxing_crashreporter_CrashReport_initNativeCrash(JNIEnv *env, jobject thiz,
                                                                jstring _path) {
    const char *path = env->GetStringUTFChars(_path, NULL);
    LOGD("-> %s", path);
    //开启crash监控
    google_breakpad::MinidumpDescriptor descriptor(path);
    static google_breakpad::ExceptionHandler eh(descriptor, NULL, DumpCallback,
                                                NULL, true, -1);
    env->ReleaseStringUTFChars(_path, path);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wangxingxing_crashreporter_CrashReport_testNativeCrash(JNIEnv *env, jobject thiz) {
    int *p = NULL;
    *p = 10;
}