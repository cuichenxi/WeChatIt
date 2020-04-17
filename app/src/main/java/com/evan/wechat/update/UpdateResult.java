/*
 * ************************************************************
 * 文件：UpdateResult.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2020年04月15日 12:56:28
 * 上次修改时间：2020年02月12日 16:00:57
 * 作者：大路
 * Copyright (c) 2020
 * ************************************************************
 */

package com.evan.wechat.update;

import android.text.TextUtils;

import java.io.Serializable;

public class UpdateResult implements Serializable {

    /**
     * content : {"upgrade":true,"forceUpgrade":false,"totalUpgrade":true,"downloadUrl":"http://www.lengshengren.com:8082/etfapp/app/downloadApp/3321c024-476a-4634-bf8e-5dfeff034ffc/1/1","upgradeMsg":"【电子化巡店】\n- 总部、平台、连锁支持分级定制巡店单\n- 移动巡店单支持单选、多选、文本，并且均支持打分和拍照\n- 巡店人，可移动填写分配的巡店单，或在自行巡店时主动创建\n- 巡店结果自动发送给门店店长，支持按项反馈","versionName":"1.8.2","upgradeDate":"2018-12-20"}
     * code : 1
     * msg : 成功
     */

    public ContentBean content;
    public int code;
    public String msg;

    public static class ContentBean implements Serializable{
        /**
         * upgrade : true
         * forceUpgrade : false
         * totalUpgrade : true
         * downloadUrl : http://www.lengshengren.com:8082/etfapp/app/downloadApp/3321c024-476a-4634-bf8e-5dfeff034ffc/1/1
         * upgradeMsg : 【电子化巡店】
         * - 总部、平台、连锁支持分级定制巡店单
         * - 移动巡店单支持单选、多选、文本，并且均支持打分和拍照
         * - 巡店人，可移动填写分配的巡店单，或在自行巡店时主动创建
         * - 巡店结果自动发送给门店店长，支持按项反馈
         * versionName : 1.8.2
         * upgradeDate : 2018-12-20
         */

        public boolean upgrade;
        public boolean forceUpgrade;
        public boolean totalUpgrade;
        public String downloadUrl;
        public String upgradeMsg;
        public String versionName;
        public String upgradeDate;
        public String appSize;
        public int versionCode;
        public boolean isIgnorable;
        public String MD5;

        public boolean hasUpdate() {
            return upgrade && !TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(upgradeMsg);
        }
    }
}
