package com.example.als;

import android.os.Looper;
import android.util.Log;

public class DebugHook {
    public static void debugNativeCall(int arg) {
        // 检查线程环境
        Log.d("NativeDebug", "Thread: " + Thread.currentThread().getName());
        Log.d("NativeDebug", "Calling with arg: " + arg);

        // 检查Looper状态（关键！）
        boolean hasLooper = Looper.getMainLooper() != null;
        Log.d("NativeDebug", "Has main looper: " + hasLooper);
    }
}
