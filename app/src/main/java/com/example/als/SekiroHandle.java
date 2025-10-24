package com.example.als;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.iinti.sekiro3.business.api.fastjson.JSONObject;
import cn.iinti.sekiro3.business.api.interfaze.ActionHandler;
import cn.iinti.sekiro3.business.api.interfaze.SekiroRequest;
import cn.iinti.sekiro3.business.api.interfaze.SekiroResponse;
import de.robv.android.xposed.XposedBridge;

public class SekiroHandle implements ActionHandler {
    /*
    * 直接把加密代码扣一下,使用sekiro开启http服务
    * */
    @Override
    public String action() {
        return "handle016";
    }
    //    http://81.71.152.244:5612/business/invoke?group=tx_group&action=handle016
    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        String coord = sekiroRequest.getString("coord");
        String key = sekiroRequest.getString("key");
        String content = getContent(coord,key);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("content", content);
        sekiroResponse.success(jsonObject);
    }

    public static String getContent(String coord,String key) {
        String content = "";
        // 注：原代码中手动赋值 coord 会覆盖入参，若需使用入参可删除此句
        //coord = "39,123|150,98|240,115";
        //key = "FQRjFYSGa0cybaMw";

        XposedBridge.log("coord:"+coord);
        XposedBridge.log("key:"+key);

        // 1. 创建存储 Point 对象的 List 集合
        List<Point> pointList = new ArrayList<>();

        // 2. 检查 coord 不为空，避免空指针异常
        if (coord != null && !coord.trim().isEmpty()) {
            // 3. 按 "|" 分割坐标字符串，得到单个坐标对（如 "39,123"）
            String[] coordinatePairs = coord.split("\\|");

            // 4. 遍历每个坐标对，解析 x、y 并创建 Point 对象
            for (String pair : coordinatePairs) {
                // 按 "," 分割单个坐标对，得到 x 和 y 的字符串
                String[] xy = pair.split(",");
                // 校验分割结果是否为 2 位（避免格式错误导致数组越界）
                if (xy.length == 2) {
                    try {
                        // 5. 将字符串转为 double 类型（坐标可能为小数，需用 double 匹配 Point 类属性）
                        double x = Double.parseDouble(xy[0].trim());
                        double y = Double.parseDouble(xy[1].trim());

                        // 6. 创建 Point 对象并设置 x、y 值
                        Point point = new Point();
                        point.setX(x);
                        point.setY(y);

                        // 7. 将 Point 对象添加到 List 中
                        pointList.add(point);
                    } catch (NumberFormatException e) {
                        // 若坐标无法转为数字，可捕获异常（如日志打印），避免整体流程中断
                        e.printStackTrace();
                    }
                }
            }

            // 8. 使用 Gson 将 List<Point> 转为 JSON 字符串
            Gson gson = new Gson();
            content = gson.toJson(pointList);
            XposedBridge.log("content:"+content);
            content = o0OO00O.OooO00o(content,key);
        }

        return content;
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
