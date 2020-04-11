/*
 * ************************************************************
 * 文件：SendMessageHandler.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2020年04月11日 12:30:24
 * 上次修改时间：2020年04月11日 12:30:23
 * 作者：大路
 * Copyright (c) 2020
 * ************************************************************
 */

package com.evan.wechat;

import android.text.TextUtils;

import com.evan.wechat.xposed.WXMessageUtils;
import com.virjar.sekiro.api.SekiroRequest;
import com.virjar.sekiro.api.SekiroRequestHandler;
import com.virjar.sekiro.api.SekiroResponse;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import external.com.alibaba.fastjson.JSON;

class SendMessageHandler implements SekiroRequestHandler {
    private final XC_LoadPackage.LoadPackageParam lpparam;

    public SendMessageHandler(XC_LoadPackage.LoadPackageParam lpparam) {
        this.lpparam = lpparam;
    }

    @Override
    public void handleRequest(SekiroRequest sekiroRequest, SekiroResponse sekiroResponse) {
        String roomId = sekiroRequest.getString("roomId");
        String content = sekiroRequest.getString("content");
        XposedBridge.log("SendMessage=" + JSON.toJSONString(sekiroRequest.getJsonModel()));
        if (TextUtils.isEmpty(roomId) || TextUtils.isEmpty(content)) {
            return;
        }
        int msgType = 1;
        switch (msgType) {
            case 0:
            case 1:
                WXMessageUtils.sentTextMessage(this.lpparam, content, roomId);
                break;
        }
        sekiroResponse.success("发送成功");
    }
}
