//
// Created by zgh on 2025/8/19.
//


#ifndef DEMOSO_LOG_H
#define DEMOSO_LOG_H

#include <android/log.h>
#define  TAG    "nativehook"

//LOGI("App started");  // 简单消息
//LOGI("User %s logged in, id: %d", "John", 123);  // 带格式的消息
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG,__VA_ARGS__)


#endif //DEMOSO_LOG_H
