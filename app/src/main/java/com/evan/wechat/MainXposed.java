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

import com.evan.wechat.xposed.WechatUtils;

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
        XposedBridge.log("进入微信进程：" + lpparam.processName);
        //调用 hook数据库插入。
        hookDatabaseInsert(lpparam);
    }

    //hook数据库插入操作
    private void hookDatabaseInsert(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> classDb = XposedHelpers.findClassIfExists(WECHAT_DATABASE_PACKAGE_NAME, loadPackageParam.classLoader);
        if (classDb == null) {
            XposedBridge.log("hook数据库insert操作：未找到类" + WECHAT_DATABASE_PACKAGE_NAME);
            return;
        }
        XposedHelpers.findAndHookMethod(classDb,
                "insertWithOnConflict",
                String.class, String.class, ContentValues.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        String tableName = (String) param.args[0];
                        ContentValues contentValues = (ContentValues) param.args[2];
                        if (tableName == null || tableName.length() == 0 || contentValues == null) {
                            return;
                        }
                        //过滤掉非聊天消息
                        if (!tableName.equals("message")) {
                            return;
                        }
                        //打印出日志
                        printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);
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
                            String[] split = strContent.split("\t");
                            String contentId = split[0];
                            String content = split[1];
                            WechatUtils.replyTextMessage707(loadPackageParam, content, strTalker);
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
