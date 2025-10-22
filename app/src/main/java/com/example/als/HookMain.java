package com.example.als;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.UUID;

import cn.iinti.sekiro3.business.api.SekiroClient;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    private static final String TAG = "als";

    //private static final String XP_PACKAGE_NAME = "com.example.als";
    private static final String YRX_PACKAGE_NAME = "com.yuanrenxue.challenge";

    private static boolean isSekiroInitialized = false; // 新增静态标记

    private static final String TARGET_CLASS = "com.yuanrenxue.challenge.fragment.challenge.ChallengeFiveFragment";


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        if(lpparam.processName.equals(lpparam.packageName)) {
            showToast(lpparam.packageName + " coming");
            if(lpparam.packageName.equals(YRX_PACKAGE_NAME)){
                try {
                    Log.i(TAG,"handleLoadPackage:start");


                    // Hook ChallengeFiveFragment 中生成 s 和 s1 的方法
                    XposedHelpers.findAndHookMethod(
                            TARGET_CLASS,
                            lpparam.classLoader,
                            "initListeners", // 生成参数的代码在 initListeners 方法中
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    // 获取当前 Fragment 实例（this 指向 ChallengeFiveFragment）
                                    Object fragment = param.thisObject;
                                    // 获取页码（OooO0o0 是页码变量，可能为 private，需反射获取）
                                    Field pageField = XposedHelpers.findField(fragment.getClass(), "OooO0o0");
                                    int page = (int) pageField.get(fragment);
                                    Log.i(TAG,"page:"+page);
                                    Log.i(TAG, Log.getStackTraceString(new Throwable()));
                                    connectServer2(fragment,pageField,lpparam);
                                }
                            }
                    );




                    Log.i(TAG,"handleLoadPackage:end");

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    private static void showToast(String msg){
        new Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Context context = AndroidAppHelper.currentApplication().getApplicationContext();
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                }, 3000);
    }

    private static void connectServer2(Object fragment,Field pageField,XC_LoadPackage.LoadPackageParam lpparam){

        if (isSekiroInitialized) {
            Log.i(TAG, "Sekiro 已初始化，无需重复连接");
            return;
        }

        new SekiroClient("tx_group", UUID.randomUUID().toString(),"81.71.152.244",5612)
                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) ->{
                    //handlerRegistry.registerSekiroHandler(new TestHandle());
                    //handlerRegistry.registerSekiroHandler(new TwoHandle());
                    handlerRegistry.registerSekiroHandler(new SekiroHandle(fragment,pageField,lpparam));
                }).start();

        Log.i(TAG, "sekiro:start");
        isSekiroInitialized = true; // 标记为已初始化

    }

}
