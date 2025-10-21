package com.example.als;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;

public class SekiroHandle implements ActionHandler {


    private static Handler mainHandler = new Handler(Looper.getMainLooper());


    @Override
    public String action() {
        return "handle003";
    }
    //    http://81.71.152.244:5612/business/invoke?group=tx_group&action=handle003&page=1
    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {

        CompletableFuture<byte[]> future = new CompletableFuture<>();
        // 切换到主线程执行
        // so中有主线程检测
        mainHandler.post(() -> {
            try {
                int page = sekiroRequest.getIntValue("page");
                Log.i("handleRequest","page:"+page);
                byte[] signBytes = new NativeFunc().sign(page);
                DebugHook.debugNativeCall(2);
                future.complete(signBytes);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        try {
            byte[] signBytes = future.get(5, TimeUnit.SECONDS);
            outputByteArr(signBytes);
            String s = ChallengeThreeFragment.OooO0oO(signBytes);
            Log.i("handleRequest",s);
            //JSONObject result = Store.callEncrypt(page,time);
            //Log.i("handleRequest","result:"+result.toString());;
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sign", s);
            sekiroResponse.success(jsonObject);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }

    }

    public static void outputByteArr(byte[] originalSign){
        // 用 for 循环遍历，打印每个字节的十进制值
        StringBuilder decimalLog = new StringBuilder();
        decimalLog.append("[");
        for (int i = 0; i < originalSign.length; i++) {
            decimalLog.append(originalSign[i]); // 直接取 byte 元素（十进制）
            if (i != originalSign.length - 1) {
                decimalLog.append(", "); // 非最后一个元素，加逗号分隔
            }
        }
        decimalLog.append("]");

        // 打印结果
        Log.i("originalSignArr:",decimalLog.toString());
    }

}
