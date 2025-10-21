//
// Created by zgh on 2025/8/19.
// 该头文件来源于:LSPosed/core/src/main/jni/src/native_api.h
//

#ifndef DEMOSO_NATIVE_API_H
#define DEMOSO_NATIVE_API_H

#include <stdint.h>

//typedef 是 C/C++ 中的一个关键字，用于为已有的数据类型（包括基本类型、自定义类型、指针类型等）定义一个新的别名（别称）。其核心作用是简化复杂类型的表示、提高代码可读性和增强代码可维护性。
//typedef 原类型 新别名;

//为 int 类型定义别名 Integer
//typedef int Integer;

//为 "int*" 定义别名 IntPtr
//typedef int* IntPtr;

//原函数指针声明：指向 "int(int, int)" 类型函数的指针
//int (*FuncPtr)(int, int);

//typedef int (*MyFuncPtr)(int, int);
//别名 MyFuncPtr 代表 "int(*)(int, int)" 类型  [int 表示函数的返回值类型] [* 表示这是一个指针类型（指向函数的指针）] [括号 () 用于强调 * 与函数的绑定关系（避免语法歧义）]
//int(*) 是函数指针类型的部分语法，用于表示 “指向返回值为 int 类型的函数的指针”


// 定义钩子函数类型：用于"挂钩"（替换）目标函数
// 参数：
// - func：目标函数的地址（要被替换的函数）
// - replace：替换函数的地址（新的实现）
// - backup：输出参数，用于保存原函数的地址（后续可通过备份调用原函数）
// 返回值：int类型，通常表示挂钩是否成功（0为成功，非0为失败）
typedef int (*HookFunType)(void *func, void *replace, void **backup);
// 定义解除钩子函数类型：用于"脱钩"（恢复）原函数
// 参数：func：已挂钩的函数地址
// 返回值：int类型，通常表示脱钩是否成功
typedef int (*UnhookFunType)(void *func);
// 模块加载回调函数类型：当一个动态库（.so）被加载时触发
// 参数：
// - name：被加载的库名
// - handle：库的句柄（可用于通过dlsym获取库中的函数）
typedef void (*NativeOnModuleLoaded)(const char *name, void *handle);

// 钩子系统的API入口结构体：封装钩子功能的核心接口
typedef struct {
    uint32_t version;   // 版本号（用于兼容性判断）
    HookFunType hook_func;  // 挂钩函数
    UnhookFunType unhook_func;  // 脱钩函数
} NativeAPIEntries;

// 初始化函数类型：用于初始化钩子系统
// 参数：entries：钩子系统的API入口（包含挂钩/脱钩函数）
// 返回值：模块加载的回调函数（NativeOnModuleLoaded）
typedef NativeOnModuleLoaded (*NativeInit)(const NativeAPIEntries *entries);
#endif //DEMOSO_NATIVE_API_H
