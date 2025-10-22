package com.example.als;

import android.util.Log;

import java.lang.reflect.Field;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SekiroHandle implements ActionHandler {

    private static final String TAG = "als";

    public Object fragment;
    public Field pageField;
    public XC_LoadPackage.LoadPackageParam lpparam;

    public SekiroHandle(Object fragment, Field pageField, XC_LoadPackage.LoadPackageParam lpparam) {
        this.fragment = fragment;
        this.pageField = pageField;
        this.lpparam = lpparam;
    }

    @Override
    public String action() {
        return "handle005";
    }
    //    http://81.71.152.244:5612/business/invoke?group=tx_group&action=handle005&page=3
    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        try {
            // 从请求中获取页码（默认 1）
            int inputPage = sekiroRequest.getIntValue("page");
            // 更新 Fragment 中的页码
            pageField.set(fragment, inputPage);
            Log.i(TAG, "inputPage:"+inputPage);
            // 生成 content（即 s）：页码 + 固定字符串 + 时间戳
            String fixedStr = (String) XposedHelpers.callStaticMethod(
                    lpparam.classLoader.loadClass("o0O000oo.o000oOoO"),
                    "OooO00o",
                    0x3638F15D2B90E009L // 固定常量（下拉刷新和加载更多可能不同，需确认）
            );
            Log.i(TAG, "fixedStr:"+fixedStr);
            long timestamp = System.currentTimeMillis();
            String content = inputPage + fixedStr + timestamp;
            Log.i(TAG, "content:"+content);
            // 生成 sign（即 s1）：调用 DexUtils 中的加密方法
            Object dexUtils = XposedHelpers.callStaticMethod(
                    lpparam.classLoader.loadClass("com.yuanrenxue.challenge.four.DexUtils"),
                    "OooO00o"
            );
            String sign = (String) XposedHelpers.callMethod(
                    dexUtils,
                    "OooO0O0",
                    XposedHelpers.callMethod(fragment, "getContext"), // context
                    fragment, // object0（当前 Fragment）
                    content // 待签名的字符串 s
            );
            Log.i(TAG, "sign:"+sign);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            jsonObject.put("sign", sign);
            //jsonObject.put("timestamp", timestamp);
            // 将结果返回给 Sekiro 客户端
            sekiroResponse.success(jsonObject);
        } catch (Exception e) {
            Log.e(TAG, "生成参数失败", e); // 打印完整堆栈
            sekiroResponse.failed("生成参数失败：" + e.getMessage());
        }

    }

}
