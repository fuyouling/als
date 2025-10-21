package com.example.als;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.UUID;

import cn.iinti.sekiro3.business.api.SekiroClient;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {
    private static final String TAG = "als";

    private static final String XP_PACKAGE_NAME = "com.example.als";
    private static final String YRX_PACKAGE_NAME = "com.yuanrenxue.challenge";


    private static final String TARGET_CLASS = "com.yuanrenxue.challenge.three.ChallengeThreeNativeLib";
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if(lpparam.processName.equals(lpparam.packageName)) {
            showToast(lpparam.packageName + " coming");
            if(lpparam.packageName.equals(YRX_PACKAGE_NAME)){
                try {
                    Log.i(TAG,"handleLoadPackage:start hook java");
                    Class<?> ChallengeThreeNativeLib = Class.forName(
                            TARGET_CLASS,
                            true,  // 第二个参数为true，表示加载后立即初始化
                            lpparam.classLoader
                    );

                    connectServer2();


                    Log.i(TAG,"handleLoadPackage:end hook java sekiro start");

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

    private static void connectServer2(){

        new SekiroClient("tx_group", UUID.randomUUID().toString(),"81.71.152.244",5612)
                .setupSekiroRequestInitializer((sekiroRequest, handlerRegistry) ->{
                    //handlerRegistry.registerSekiroHandler(new TestHandle());
                    //handlerRegistry.registerSekiroHandler(new TwoHandle());
                    handlerRegistry.registerSekiroHandler(new SekiroHandle());
                }).start();

        Log.i(TAG, "sekiro:start");

    };
}
