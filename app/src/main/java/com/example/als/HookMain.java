package com.example.als;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.iinti.sekiro3.business.api.SekiroClient;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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

                connectServer2();

                try {
                    // 原始类名：com.yuanrenxue.challenge.captcha.widget.WordImageView（混淆后可能为OooO）
                    Class<?> wordImageViewClass = XposedHelpers.findClass(
                            "com.yuanrenxue.challenge.captcha.widget.WordImageView",
                            lpparam.classLoader
                    );

                    // Hook onTouchEvent方法，监听点击坐标
                    XposedHelpers.findAndHookMethod(
                            wordImageViewClass,
                            "onTouchEvent",
                            MotionEvent.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    MotionEvent event = (MotionEvent) param.args[0];
                                    // 只处理按下事件（ACTION_DOWN）
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        float x = event.getX(); // 原始X坐标（像素）
                                        float y = event.getY(); // 原始Y坐标（像素）
                                        XposedBridge.log("[验证码Hook] 原始点击坐标（像素）: x=" + x + ", y=" + y);
                                    }
                                }
                            }
                    );

                } catch (Throwable e) {
                    XposedBridge.log("[验证码Hook] Hook WordImageView触摸事件失败: " + e.getMessage());
                }

                try {
                    // 类全限定名
                    String className = "o00o0oo.o0OO00O";
                    // 方法名
                    String methodName = "OooO00o";
                    // 方法参数类型（两个String参数）
                    Class<?>[] paramTypes = new Class[]{String.class, String.class};

                    // Hook静态方法
                    XposedHelpers.findAndHookMethod(
                            className,
                            lpparam.classLoader,
                            methodName,
                            String.class, String.class,
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    // 获取输入参数
                                    String s = (String) param.args[0]; // 待加密的原始字符串
                                    String s1 = (String) param.args[1]; // 加密密钥

                                    XposedBridge.log("OooO00o 调用前：");
                                    XposedBridge.log("原始字符串 s = " + s);
                                    XposedBridge.log("密钥 s1 = " + s1);

                                    String  jsonStr = s;
                                    // 1. 初始化Gson对象
                                    Gson gson = new Gson();
                                    // 2. 将JSON字符串解析为List<Map>（适配{x,y}结构）
                                    List<Map<String, Double>> pointList = gson.fromJson(
                                            jsonStr,
                                            new TypeToken<List<Map<String, Double>>>() {}.getType()
                                    );

                                    // 3. 拼接坐标字符串（格式：x1,y1|x2,y2|...）
                                    StringBuilder coordinateSb = new StringBuilder();
                                    for (int i = 0; i < pointList.size(); i++) {
                                        Map<String, Double> pointMap = pointList.get(i);
                                        // 获取x、y值并转为整数（因小数点后为0，直接强转不丢失数据）
                                        int x = pointMap.get("x").intValue();
                                        int y = pointMap.get("y").intValue();

                                        // 拼接当前坐标对
                                        coordinateSb.append(x).append(",").append(y);
                                        // 非最后一个坐标对，添加"|"分隔符
                                        if (i != pointList.size() - 1) {
                                            coordinateSb.append("|");
                                        }
                                    }

                                    String content = SekiroHandle.getContent(coordinateSb.toString(),s1);
                                    XposedBridge.log("sekiro:" + content);
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    // 获取方法返回值（加密后的Base64字符串）
                                    String result = (String) param.getResult();

                                    XposedBridge.log("OooO00o 调用后：");
                                    XposedBridge.log("加密结果 = " + result);

                                    // 可选：修改返回值
                                    // param.setResult("被替换的加密结果");
                                }
                            }
                    );
                } catch (Throwable e) {
                    XposedBridge.log("Hook OooO00o 失败：" + e.getMessage());
                    e.printStackTrace();
                }

                try {
                    // 获取混淆类 o00o0Oo 的 Class 对象
                    Class<?> convertClass = XposedHelpers.findClass(
                            "o00o0oo.oo0o0Oo",
                            lpparam.classLoader
                    );


                    // 2. Hook OooO0O0 方法（推测：像素转 dp）
                    XposedHelpers.findAndHookMethod(
                            convertClass,
                            "OooO0O0",
                            Context.class,    // 第一个参数：上下文
                            Float.class,      // 第二个参数：待转换的数值（如像素值）
                            new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    Context context = (Context) param.args[0];
                                    Float inputValue = (Float) param.args[1];
                                    // 输出输入参数
                                    XposedBridge.log("[坐标转换Hook] 数值=" + inputValue);
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    int result = (int) param.getResult();
                                    // 输出返回结果
                                    XposedBridge.log("[坐标转换Hook] 输出=" + result);
                                }
                            }
                    );

                } catch (Throwable e) {
                    XposedBridge.log("[坐标转换Hook] Hook 失败: " + e.getMessage());
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
