//
// Created by zgh on 2025/8/21.
//
#include <jni.h>
#include <string>
#include "log.h"
#include "elf.h"

// 参数：JNIEnv*、jclass、jbyteArray、jlong；返回值：jbyteArray
typedef jbyteArray(*encrypt_func_type)(JNIEnv*, jobject, jint);

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_example_als_NativeFunc_sign(
        JNIEnv* env,
        jobject obj ,
        jint key) {

    LOGI("comming in native");

    // 验证 env 是否有效（检查是否能调用 JNI 函数）
    if (env != nullptr) {
        LOGI("get_valid_jni_env: JNIEnv 有效！env = %p", env);
    }
    if(obj != nullptr){
        LOGI("is_obj_valid: obj 有效！");
    }

    // 获取函数指针,偏移量可以通过ida查看,内存中需要加载相对应的so,否则找不到
    // ida中左侧点击方法名称就可查看偏移量
    void* encrypt = ElfUtils::GetModuleOffset("libthree.so", 0x2104C);
    // 类型转换：将void*转换为正确的函数指针类型
    auto encrypt_func = reinterpret_cast<encrypt_func_type>(encrypt);
    if (encrypt_func == nullptr) {
        // 处理函数指针获取失败的情况
        //return env->NewStringUTF("Failed to get encrypt function");
        LOGI("Failed to get encrypt function");
        return nullptr;
    }else {
        jbyteArray result = encrypt_func(env, obj, key);
        return result;
    }
}