//
// Created by zgh on 2025/8/21.
//
#include <jni.h>
#include <string>
#include "log.h"
#include "elf.h"


typedef jbyteArray(*encrypt_func_type)(JNIEnv*, jclass, jbyteArray, jlong);

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_example_als_NativeFunc_encrypt(
        JNIEnv* env,
        jclass clazz ,
        jbyteArray data, jlong key) {

    //LOGI("comming in native");
    void* encrypt = ElfUtils::GetModuleOffset("libtwo.so", 0x1BC8);
    auto encrypt_func = reinterpret_cast<encrypt_func_type>(encrypt);
    if (encrypt_func == nullptr) {
        LOGI("Failed to get encrypt function");
        return nullptr;
    }else {
        jbyteArray result = encrypt_func(env, clazz, data, key);
        return result;
    }
}