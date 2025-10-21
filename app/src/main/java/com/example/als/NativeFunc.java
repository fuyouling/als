package com.example.als;

public class NativeFunc {
    static{
        System.loadLibrary("nativehook");
    }

    //调用该函数时,内存中需要加载相对应的so,否则找不到
    public native byte[] sign(int arg1);
}