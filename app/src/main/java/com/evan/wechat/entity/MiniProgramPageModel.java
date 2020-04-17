/*
 * ************************************************************
 * 文件：MiniProgramPageModel.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2020年04月14日 15:47:22
 * 上次修改时间：2020年04月14日 15:47:22
 * 作者：大路
 * Copyright (c) 2020
 * ************************************************************
 */

package com.evan.wechat.entity;

public class MiniProgramPageModel {

    /**
     * miniprogrampage : {"title":"高济药急送- 五官用药","appid":"wx20f5c28b2a296bba","pagepath":"modules/o2o/search/index?searchKeyWord=五官用药","thumb_url":"http://mmbiz.qpic.cn/mmbiz_pn"}
     */

    public MiniprogrampageBean miniprogrampage;

    public static class MiniprogrampageBean {
        /**
         * title : 高济药急送- 五官用药
         * appid : wx20f5c28b2a296bba
         * pagepath : modules/o2o/search/index?searchKeyWord=五官用药
         * thumb_media_id : ezp6y0Kavf0hkPOmoq3TMb0zjeKyLW3Bb9ozldY5paU
         * thumb_url : http://mmbiz.qpic.cn/mmbiz_pn
         */

        public String title;
        public String appid;
        public String pagepath;
        public String thumb_media_id;
        public String thumb_url;
        public String webpageUrl;
    }
}
