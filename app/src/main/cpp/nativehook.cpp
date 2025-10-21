#include <jni.h>    // JNI（Java Native Interface）头文件，用于Java与C交互
#include <string>
#include <dlfcn.h> // 动态链接库操作函数（如dlsym，用于获取库中函数地址）,例如:dlsym
#include "native_api.h"
#include "log.h"

// 全局钩子函数指针（后续会通过native_init初始化）
static HookFunType hook_func = nullptr;

// 备份原函数的指针（用于在替换函数中调用原逻辑）
int (*backup)();

// 1. encrypt1函数的替换实现
int fake() {
    LOGI("native function: fake");
    return backup();
}

// 备份encrypt1函数
FILE *(*backup_fopen)(const char *filename, const char *mode);
// 2. fopen函数的替换实现（标准库文件打开函数）
FILE *fake_fopen(const char *filename, const char *mode) {
    LOGI("native function: fake_fopen");
    // 拦截包含"banned"的文件
    if (strstr(filename, "banned")) return nullptr;
    // 正常文件调用原fopen
    return backup_fopen(filename, mode);
}

// 备份JNI的FindClass函数
jclass (*backup_FindClass)(JNIEnv *env, const char *name);
// 3. JNI的FindClass函数的替换实现（用于查找Java类）
jclass fake_FindClass(JNIEnv *env, const char *name)
{
    LOGI("native function: fake_FindClass");
    // 拦截特定类 , 禁止加载该类
    if(!strcmp(name, "dalvik/system/BaseDexClassLoader"))
        return nullptr;
    return backup_FindClass(env, name);
}

// 当动态库（.so）被加载时触发的回调函数
void on_library_loaded(const char *name, void *handle) {
    LOGI("native function: on_library_loaded");
    // hooks on `libtarget.so` 只对"libtwo.so"进行挂钩
    //if (std::string(name).ends_with("libtwo.so")) {
    //    LOGI("native function: on_library_loaded libtwo.so");
    //    // 通过dlsym从libtwo.so中获取"encrypt1"函数的地址
    //    //void *target = dlsym(handle, "encrypt1");
    //    // 对encrypt1函数挂钩：用fake替换它，同时备份原函数到backup
    //    //hook_func(target, (void *) fake, (void **) &backup);
    //    LOGI("encrypt1 function address: %p", target);
    //}
}

//extern "C": 告诉编译器按照 C 语言的函数命名规则编译该函数（避免 C++ 的名称修饰，确保 JVM 能正确找到该函数）
//[[gnu::visibility("default")]]: GCC 编译器属性，设置函数可见性为 "默认"，确保该函数能被库外部（如 JVM）找到
//[[gnu::used]]: 告诉编译器即使该函数看似未被使用，也不要优化删除它（确保 JVM 始终能调用到）
extern "C" [[gnu::visibility("default")]] [[gnu::used]]
//JNI_OnLoad 是 JNI 规范中定义的特殊函数，当 Java 通过System.loadLibrary()加载 Native 库（.so 文件）时，虚拟机（JVM）会首先调用这个函数，用于初始化 Native 库并返回支持的 JNI 版本。
//jint: 函数返回值类型，是 JNI 中定义的整数类型（对应 Java 的 int）
//JavaVM *jvm: 参数 1，Java 虚拟机（JVM）的指针，全局唯一，可用于获取 JNI 环境（JNIEnv）
//void*: 参数 2，预留参数，通常无用，所以未命名,void* 表示任意类型的指针
jint JNI_OnLoad(JavaVM *jvm, void*) {
    LOGI("native function: JNI_OnLoad");
    //获取JNI环境
    //JNIEnv *env: 指向 JNI 环境的指针，包含了所有 JNI 函数（如操作 Java 对象、调用 Java 方法等）
    //jvm->GetEnv(): 通过 JVM 指针获取当前线程的 JNI 环境
    //JNI_VERSION_1_6: 指定要使用的 JNI 版本（这里是 Java 6 对应的 JNI 版本）
    //c++语法,对象访问成员".",指针对象访问成员"->"
    //env 是 JNIEnv* 类型（指向 JNIEnv 结构体的指针）
    //&env 则是 JNIEnv** 类型（指向 “JNIEnv* 指针” 的二级指针）
    //jvm->GetEnv 函数的第一个参数要求是 void** 类型（函数声明为 jint GetEnv(void**penv, jint version);）
    //(void**)：这是强制类型转换运算符，作用是将紧跟其后的变量或表达式的类型转换为 void**
    //(void**)&env 的作用是将 JNIEnv** 类型的 &env 转换为 void** 类型，以匹配 GetEnv 函数的参数要求
    //GetEnv 函数的设计目的是返回一个通用的 JNI 环境指针，而 void** 作为通用二级指针类型，可以接收任何类型的二级指针（如 JNIEnv**、void*** 等）。这种设计保证了函数的通用性，使其可以适用于不同版本的 JNI 环境。
    //JNI_VERSION_1_6: 指定要使用的 JNI 版本（这里是 Java 6 对应的 JNI 版本）,目前最新的也就JNI_VERSION_1_8,需要对应版本的jvm支持
    JNIEnv *env = nullptr;
    jvm->GetEnv((void **)&env, JNI_VERSION_1_6);
    // 对JNI的FindClass函数挂钩：用fake_FindClass替换它，备份到backup_FindClass
    //hook_func((void *)env->functions->FindClass, (void *)fake_FindClass, (void **)&backup_FindClass);
    return JNI_VERSION_1_6;
}

extern "C" [[gnu::visibility("default")]] [[gnu::used]]
// 钩子系统的初始化函数（外部会调用此函数传入钩子API）
NativeOnModuleLoaded native_init(const NativeAPIEntries *entries) {
    LOGI("native function: native_init");
    // 保存钩子函数（从外部传入的API入口中获取）
    hook_func = entries->hook_func;
    // system hooks, 对标准库的fopen函数挂钩：用fake_fopen替换它，备份到backup_fopen
    //hook_func((void*) fopen, (void*) fake_fopen, (void**) &backup_fopen);
    // 返回模块加载回调（告诉系统：当有库加载时调用on_library_loaded）
    return on_library_loaded;
}