package com.example.als;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.nio.charset.StandardCharsets;
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
        return "handle002";
    }
    //http://81.71.152.244:5612/business/invoke?group=tx_group&action=handle002&page=1&time=1718787645123
    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        int page = sekiroRequest.getIntValue("page");
        long time = sekiroRequest.getLongValue("time");
        Log.i("handleRequest","page:"+page+" time:"+time);;

        JSONObject jsonObject = new JSONObject();
        //long v = 1718787645123L;
        byte[] bytearray= String.format("%d:%d", page, time).getBytes(StandardCharsets.UTF_8);
        //Log.i(TAG, "bytearray:"+ Arrays.toString(bytearray));

        byte[] encryptedBytes = NativeFunc.encrypt(bytearray, time);
        String base64Result = Base64.encodeToString(encryptedBytes, 10);

        //Log.i(TAG, "base64Result:"+base64Result);
        jsonObject.put("base64Result", base64Result);


        //Log.i("handleRequest","result:"+result.toString());;
        sekiroResponse.success(jsonObject);


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
