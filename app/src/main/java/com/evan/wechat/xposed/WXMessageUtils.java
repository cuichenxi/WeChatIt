/*
 * ************************************************************
 * 文件：WXMessageUtils.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2018年08月19日 20:03:59
 * 上次修改时间：2018年08月19日 20:03:59
 * 作者：大路
 * Copyright (c) 2018
 * ************************************************************
 */

package com.evan.wechat.xposed;

import android.content.ContentValues;
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

public class WXMessageUtils {

    /**
     * 发送文本信息
     *
     * @param loadPackageParam
     * @param strContent
     * @param chatRoomId
     */
    public static void sentTextMessage(XC_LoadPackage.LoadPackageParam loadPackageParam,
                                       String strContent, final String chatRoomId) {
        XposedBridge.log("发送消息内容：content:" + strContent + ",chatRoomId:" + chatRoomId);
        if (strContent == null || chatRoomId == null
                || strContent.length() == 0 || chatRoomId.length() == 0 || loadPackageParam == null) {
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

    public static void handleMessageRecall(XC_LoadPackage.LoadPackageParam loadPackageParam, ContentValues contentValues) {
        //提取消息内容
        //1：表示是自己发送的消息
        int isSend = contentValues.getAsInteger("isSend");
        //消息内容
        String strContent = contentValues.getAsString("content");
        //说话人ID
        String strTalker = contentValues.getAsString("talker");
        int type = contentValues.getAsInteger("type");
        //收到消息，进行回复（要判断不是自己发送的、不是群消息、不是公众号消息，才回复）
        if (isSend != 1 && strTalker.endsWith("@chatroom") && !strTalker.startsWith("gh_")) {
            String content = null;
            if (strContent.contains("\n")) {
                String contentId = strContent.substring(0, strContent.indexOf("\n"));
                content = strContent.substring(contentId.length() + 1);
            } else {
                content = strContent;
            }
            sentTextMessage(loadPackageParam, content, strTalker);
        }
    }

    /**
     * public String roomId;//17595315330@chatroom//群ID
     * public String nickname;//  群名昵称
     * public String memberlist;//wxid_980dllc5zh8i22;cuichenxi8895340; 群成员
     * public String displayname;//崔陈喜、崔哥//群成员昵称
     * public String roomowner;//cuichenxi8895340//群主
     *
     * @param paramsMap
     */
    public static void handleMessageChatInfo(JSONObject paramsMap) {
        OkHttpUtils.postString()
                .url("10.8.131.76:5602")
                .content(paramsMap.toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        XposedBridge.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        XposedBridge.log(response);
                    }
                });
    }

}
