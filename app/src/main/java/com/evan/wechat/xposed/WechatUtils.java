/*
 * ************************************************************
 * 文件：WechatUtils.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2018年08月19日 20:03:59
 * 上次修改时间：2018年08月19日 20:03:59
 * 作者：大路
 * Copyright (c) 2018
 * ************************************************************
 */

package com.evan.wechat.xposed;

import android.util.Log;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.Call;

public class WechatUtils {
    //回复文本消息
    public static void replyTextMessage(XC_LoadPackage.LoadPackageParam loadPackageParam,
                                        String strContent, final String strChatroomId) {
        XposedBridge.log("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);

        if (strContent == null || strChatroomId == null
                || strContent.length() == 0 || strChatroomId.length() == 0) {
            return;
        }

        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.i", loadPackageParam.classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar,
                new Class[]{String.class, String.class, int.class, int.class, Object.class},
                strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
                    put(strChatroomId, strChatroomId);
                }});
        Object[] objectParamiVar = new Object[]{objectiVar, 0};

        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");


        //查找au.DF().a()方法
        Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.ab.o", loadPackageParam.classLoader);
        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ab.l", loadPackageParam.classLoader);
        Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);

        //调用发消息方法
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
            XposedBridge.log("invokeOriginalMethod()执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    } //回复文本消息

    public static void replyTextMessage707(XC_LoadPackage.LoadPackageParam loadPackageParam,
                                           String strContent, final String chatRoomId) {
//        strContent = strContent + "机器人8";
        XposedBridge.log("准备回复消息内容：content:" + strContent + ",chatRoomId:" + chatRoomId);

        if (strContent == null || chatRoomId == null
                || strContent.length() == 0 || chatRoomId.length() == 0) {
            return;
        }
        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.h", loadPackageParam.classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar,
                new Class[]{String.class, String.class, int.class, int.class, Object.class},
                chatRoomId, strContent, 1, 1, new HashMap<String, String>() {{
                    put(chatRoomId, chatRoomId);
                }});
        Object[] objectParamiVar = new Object[]{objectiVar, 0};

        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "aaB");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "fvA");

        //查找au.DF().a()方法
        Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.aj.p", loadPackageParam.classLoader);
        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.aj.m", loadPackageParam.classLoader);
        Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);

        //调用发消息方法
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
            Log.v("TAG", "invokeOriginalMethod()执行成功");
            XposedBridge.log("invokeOriginalMethod()执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }

    }

    public void requestData() {
        JSONObject paramsMap = new JSONObject();
        try {
            paramsMap.putOpt("errorMessage", "");
            paramsMap.putOpt("agent", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkHttpUtils.postString()
                .url("/api/v1/frontendPerfData")
                .content(paramsMap.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        XposedBridge.log(response);
                    }
                });

    }
}
