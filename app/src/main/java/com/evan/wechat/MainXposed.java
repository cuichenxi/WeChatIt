/*
 * ************************************************************
 * 文件：MainXposed.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2018年08月19日 17:06:09
 * 上次修改时间：2018年08月19日 17:06:09
 * 作者：大路
 * Copyright (c) 2018
 * ************************************************************
 */

package com.evan.wechat;

import android.content.ContentValues;
import android.text.TextUtils;

import com.evan.wechat.xposed.WXMessageUtils;
import com.virjar.sekiro.api.SekiroClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.List;
import java.util.UUID;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public final class MainXposed implements IXposedHookLoadPackage {
    //微信数据库包名称
    private static final String WECHAT_DATABASE_PACKAGE_NAME = "com.tencent.wcdb.database.SQLiteDatabase";
    //聊天精灵客户端包名称
    private static final String WECHATGENIUS_PACKAGE_NAME = "com.evan.wechat";
    //微信主进程名
    private static final String WECHAT_PROCESS_NAME = "com.tencent.mm";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        //region hook模块是否激活
        if (lpparam.packageName.equals(WECHATGENIUS_PACKAGE_NAME)) {
            //hook客户端APP的是否激活返回值。替换为true。
            Class<?> classAppUtils = XposedHelpers.findClassIfExists(WECHATGENIUS_PACKAGE_NAME + ".util.AppUtils", lpparam.classLoader);
            if (classAppUtils != null) {
                XposedHelpers.findAndHookMethod(classAppUtils,
                        "isModuleActive",
                        XC_MethodReplacement.returnConstant(true));
                XposedBridge.log("成功hook住com.evan.wechat.util.AppUtils的isModuleActive方法。");
            }
            return;
        }
        //endregion

        if (!lpparam.processName.equals(WECHAT_PROCESS_NAME)) {
            return;
        }
        SekiroClient.getInstance().connect("10.8.131.76", 5600, UUID.randomUUID().toString(), "Group_wx");
        SekiroClient.getInstance().registerHandler("sendMessage", new SendMessageHandler(lpparam));

        XposedBridge.log("进入微信进程：" + lpparam.processName);
        //调用 hook数据库插入。
        Class<?> classDb = XposedHelpers.findClassIfExists(WECHAT_DATABASE_PACKAGE_NAME, lpparam.classLoader);
        if (classDb == null) {
            XposedBridge.log("hook数据库insert操作：未找到类" + WECHAT_DATABASE_PACKAGE_NAME);
            return;
        }
        hookDatabaseInsert(classDb, lpparam);
//        hookDatabaseUpdate(classDb,lpparam);
    }

    private void hookDatabaseUpdate(Class<?> classDb, XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod(classDb,
                "updateWithOnConflict",
                String.class, ContentValues.class, String.class, String[].class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String tableName = (String) param.args[0];
                        ContentValues contentValues = (ContentValues) param.args[1];
                        String whereClause = (String) param.args[2];
                        Object whereArgs = param.args[3];
                        StringBuffer strShereArgs = new StringBuffer();
                        if (whereArgs instanceof Array) {
                            List<String> ss = (List<String>) whereArgs;
                            if (ss != null) {
                                for (String s : ss) {
                                    strShereArgs.append(s);
                                    strShereArgs.append(";");
                                }
                            }
                        }
                        int conflictAlgorithm = (int) param.args[4];
                        printInsertLog(tableName + "whereArgs=" + strShereArgs.toString(),
                                whereClause, contentValues, conflictAlgorithm);
                    }
                });
    }

    //hook数据库插入操作
    private void hookDatabaseInsert(Class<?> classDb, final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod(classDb,
                "insertWithOnConflict",
                String.class, String.class, ContentValues.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String tableName = (String) param.args[0];
                        String columnHack = (String) param.args[1];
                        ContentValues contentValues = (ContentValues) param.args[2];
                        int conflictValue = (Integer) param.args[3];
                        if (tableName == null || tableName.length() == 0 || contentValues == null) {
                            return;
                        }
                        printInsertLog(tableName, columnHack, contentValues, conflictValue);
                        //过滤掉非聊天消息
                        if (TextUtils.isEmpty(tableName)) {
                            return;
                        }
                        switch (tableName) {
                            case "rcontact":
                                if (TextUtils.equals(columnHack, "username")) {
                                    String chatroomname = contentValues.getAsString("username");
                                    String nickname = contentValues.getAsString("nickname");
                                    JSONObject paramsMap = new JSONObject();
                                    paramsMap.put("roomId", chatroomname);
                                    paramsMap.put("nickname", nickname);
                                    WXMessageUtils.handleMessageChatInfo(paramsMap);
                                }
                                break;
                            case "chatroom":
                                if (TextUtils.equals(columnHack, "chatroomname")) {
                                    String chatroomname = contentValues.getAsString("chatroomname");
                                    String memberlist = contentValues.getAsString("memberlist");
                                    String displayname = contentValues.getAsString("displayname");
                                    String roomowner = contentValues.getAsString("roomowner");
                                    JSONObject paramsMap = new JSONObject();
                                    paramsMap.put("roomId", chatroomname);
                                    paramsMap.put("memberlist", memberlist);
                                    paramsMap.put("displayname", displayname);
                                    paramsMap.put("roomowner", roomowner);
                                    WXMessageUtils.handleMessageChatInfo(paramsMap);
                                }
                                break;
                            case "message":
                                WXMessageUtils.handleMessageRecall(loadPackageParam, contentValues);
                                break;
                        }
                    }
                });


    }

    //输出插入操作日志
    private void printInsertLog(String tableName, String nullColumnHack, ContentValues contentValues, int conflictValue) {
        String[] arrayConflicValues =
                {"", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE "};
        if (conflictValue < 0 || conflictValue > 5) {
            return;
        }
        StringBuffer csb = new StringBuffer();
        if (contentValues != null) {
            for (String key : contentValues.keySet()) {
                csb.append(key);
                csb.append("=");
                csb.append(contentValues.get(key));
                csb.append("&");
            }
        }
//        Hook数据库insert:table：message；
//        nullColumnHack：msgId；CONFLICT_VALUES：
//        ；contentValues:
//        bizClientMsgId=&msgId=8&msgSvrId=5902589209244767384&talker=17595315330@chatroom
//        &content=cuichenxi8895340:
//        经济&flag=0&status=3&msgSeq=712093520&createTime=1586485939000&lvbuffer=[B@12640be
//        &isSend=0&type=1&bizChatId=-1&talkerId=31&
        XposedBridge.log("Hook数据库insert:table：" + tableName
                + "；nullColumnHack：" + nullColumnHack
                + "；CONFLICT_VALUES：" + arrayConflicValues[conflictValue]
                + "；contentValues:" + csb.toString());
    }

}
