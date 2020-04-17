/*
 * ************************************************************
 * 文件：WXMessageUtils.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2020年04月14日 15:40:12
 * 上次修改时间：2020年04月14日 15:39:53
 * 作者：大路
 * Copyright (c) 2020
 * ************************************************************
 */

package com.evan.wechat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.evan.wechat.entity.ImageEnityModel;
import com.evan.wechat.entity.MiniProgramPageModel;
import com.evan.wechat.entity.WebPageEnityModel;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import external.com.alibaba.fastjson.JSON;
import okhttp3.Call;

public class WXMessageUtils {

    /**
     * 发送文本信息
     *
     * @param strContent
     * @param wxId
     */
    public static void sendText1(final String wxId,
                                 String strContent) {
        XposedBridge.log("发送消息内容：content:" + strContent + ",chatRoomId:" + wxId);
        if (TextUtils.isEmpty(wxId) || TextUtils.isEmpty(strContent)) {
            return;
        }
        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.h", MainXposed.classLoader);
        Object objectiVar = XposedHelpers.newInstance(classiVar,
                new Class[]{String.class, String.class, int.class, int.class, Object.class},
                wxId, strContent, 1, 1, new HashMap<String, String>() {{
                    put(wxId, wxId);
                }});
        Object[] objectParamiVar = new Object[]{objectiVar, 0};

        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", MainXposed.classLoader);
        Object objectG = XposedHelpers.callStaticMethod(classG, "aaB");
        Object objectdpP = XposedHelpers.getObjectField(objectG, "fvA");

        //查找au.DF().a()方法
        Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.aj.p", MainXposed.classLoader);
        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.aj.m", MainXposed.classLoader);
        Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);

        //调用发消息方法
        try {
            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
            XposedBridge.log("invokeOriginalMethod()执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    }

    public static void sendText(final String wxId,
                                String strContent) {
        XposedBridge.log("发送消息内容：content:" + strContent + ",chatRoomId:" + wxId);
        Class h_c = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", MainXposed.classLoader);
        Object newInstance = XposedHelpers.newInstance(h_c, new Class[]{String.class, String.class, int.class}, wxId, strContent, 1);
        Class classP = XposedHelpers.findClass("com.tencent.mm.ah.p", MainXposed.classLoader);
        Class classM = XposedHelpers.findClass("com.tencent.mm.ah.m", MainXposed.classLoader);
        Object objectFej = XposedHelpers.getStaticObjectField(classP, "fej");
        try {
            XposedHelpers.callMethod(objectFej, "d", new Class[]{classM}, newInstance);
            XposedBridge.log("发送执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    }

    /**
     * 名片
     */
    public static void sendCardInfo(final String wxId, String strContent) {
        XposedBridge.log("发送个人卡片：content:" + strContent + ",chatRoomId:" + wxId);
        Class h_c = XposedHelpers.findClass("com.tencent.mm.modelmulti.h", MainXposed.classLoader);
        Object newInstance = XposedHelpers.newInstance(h_c, new Class[]{String.class, String.class, int.class}, wxId, strContent, 42);
        Class classP = XposedHelpers.findClass("com.tencent.mm.ah.p", MainXposed.classLoader);
        Class classM = XposedHelpers.findClass("com.tencent.mm.ah.m", MainXposed.classLoader);
        Object objectFej = XposedHelpers.getStaticObjectField(classP, "fej");
        try {
            XposedHelpers.callMethod(objectFej, "d", new Class[]{classM}, newInstance);
            XposedBridge.log("发送执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    }

    public static void sendImage(final String wxId, final String mediaPath, final String mediaCoverPath) {
        if (!TextUtils.isEmpty(mediaPath) && mediaPath.startsWith("http")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        File mediaPathFile = Glide.with(MainXposed.mContext)
                                .asFile()
                                .load(mediaPath)
                                .submit().get();
                        File mediaCoverFile = null;
                        String _mediaCoverPath = null;
                        if (!TextUtils.isEmpty(mediaCoverPath) && mediaCoverPath.startsWith("http")) {
                            mediaCoverFile = Glide.with(MainXposed.mContext)
                                    .asFile()
                                    .load(mediaCoverPath)
                                    .centerCrop()
                                    .submit(150, 150)
                                    .get();
                            if (mediaCoverFile != null) {
                                _mediaCoverPath = mediaCoverFile.getAbsolutePath();
                            }
                        }
                        String _mediaPath = null;
                        if (mediaPathFile != null) {
                            _mediaPath = mediaPathFile.getAbsolutePath();
                        }
//                        _mediaPath = "/sdcard/1.png";
//                        _mediaCoverPath = "/sdcard/1.png";
                        XposedBridge.log("_mediaPath=" + _mediaPath + "_mediaCoverPath=" + _mediaCoverPath);
                        final String final_mediaPath = _mediaPath;
                        final String final_mediaCoverPath = _mediaCoverPath;
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Class l_c = XposedHelpers.findClass("com.tencent.mm.as.l", MainXposed.classLoader);
                                Class o_c = XposedHelpers.findClass("com.tencent.mm.ah.p", MainXposed.classLoader);
                                Object[] objArr = new Object[2];
                                objArr[0] = XposedHelpers.newInstance(l_c, new Object[]{Integer.valueOf(3), wxId, wxId, final_mediaPath, Integer.valueOf(1), null, Integer.valueOf(1), "", final_mediaCoverPath, Boolean.TRUE, Integer.valueOf(2130837923)});
                                objArr[1] = Integer.valueOf(1);
                                XposedHelpers.callMethod(XposedHelpers.getStaticObjectField(o_c, "fej"), "a", objArr);
                                XposedBridge.log("发送执行成功");
                            }
                        });

                    } catch (Exception e) {
                        XposedBridge.log("调用微信消息回复方法异常");
                        XposedBridge.log(e);
                    }
                }
            }).start();
        }


    }

    /**
     * 网页
     * sendMiniMssage("x", "pages/productDetail/productDetail.html?id=175263&netflow=", "gh_ff83ede5a6d7", "https://www.baidu.com", "title", "path");
     *
     * @param wxid
     * @param webpageUrl
     * @param title
     * @param thumbImageURL
     */
    public static void sendWebUrl(final String wxid, String title, String description, String webpageUrl, final String thumbImageURL) {
        Class clasWebpage = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXWebpageObject", MainXposed.classLoader);
        Object newInstance = XposedHelpers.newInstance(clasWebpage);
        XposedHelpers.setObjectField(newInstance, "webpageUrl", webpageUrl);
        Class classWXMediaMessage = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage", MainXposed.classLoader);
        final Object newInstance2 = XposedHelpers.newInstance(classWXMediaMessage);
        XposedHelpers.setObjectField(newInstance2, "mediaObject", newInstance);
        XposedHelpers.setObjectField(newInstance2, "title", title);
        XposedHelpers.setObjectField(newInstance2, "description", description);
        if (TextUtils.isEmpty(thumbImageURL)) {
            Bitmap decodeFile = BitmapFactory.decodeResource(MainXposed.mContext.getResources(), R.drawable.ic_def);
            sendMedia(wxid, newInstance2, decodeFile);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(thumbImageURL) && thumbImageURL.startsWith("http")) {
                        Bitmap decodeFile = null;
                        try {
                            decodeFile = Glide.with(MainXposed.mContext)
                                    .asBitmap()
                                    .load(thumbImageURL)
                                    .centerCrop()
                                    .submit(150, 150)
                                    .get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMedia(wxid, newInstance2, decodeFile);
                    }
                }
            }).start();
        }

    }

    /**
     * 发送小程序
     * sendMiniMssage("x", "pages/productDetail/productDetail.html?id=175263&netflow=", "gh_ff83ede5a6d7", "https://www.baidu.com", "title", "path");
     *
     * @param wxid
     * @param path
     * @param userName
     * @param webpageUrl
     * @param title
     * @param thumbImageURL
     */
    public static void sendMiniMssage(final String wxid, String path, String userName
            , String webpageUrl, String title, final String thumbImageURL) {
        Class classMini = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject", MainXposed.classLoader);
        Object newInstance = XposedHelpers.newInstance(classMini);
        XposedHelpers.setObjectField(newInstance, "path", path);
        XposedHelpers.setObjectField(newInstance, "userName", userName + "@app");
        XposedHelpers.setObjectField(newInstance, "webpageUrl", webpageUrl);
        XposedHelpers.setObjectField(newInstance, "withShareTicket", false);
        Class classWXMediaMessage = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage", MainXposed.classLoader);
        final Object newInstance2 = XposedHelpers.newInstance(classWXMediaMessage);
        XposedHelpers.setObjectField(newInstance2, "mediaObject", newInstance);
        XposedHelpers.setObjectField(newInstance2, "title", title);//titleName
        if (TextUtils.isEmpty(thumbImageURL)) {
            Bitmap decodeFile = BitmapFactory.decodeResource(MainXposed.mContext.getResources(), R.drawable.ic_def);
            sendMedia(wxid, newInstance2, decodeFile);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(thumbImageURL) && thumbImageURL.startsWith("http")) {
                        Bitmap decodeFile = null;
                        try {
                            decodeFile = Glide.with(MainXposed.mContext)
                                    .asBitmap()
                                    .load(thumbImageURL)
                                    .centerCrop()
                                    .submit(150, 150)
                                    .get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMedia(wxid, newInstance2, decodeFile);
                    }
                }
            }).start();
        }

    }

    public static void sendFile(final String wxid, String title, final String thumbImageURL, String path) {
        Class classMini = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXFileObject", MainXposed.classLoader);
        Object newInstance = XposedHelpers.newInstance(classMini);
        XposedHelpers.setObjectField(newInstance, "filePath", path);
        Class classWXMediaMessage = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage", MainXposed.classLoader);
        final Object newInstance2 = XposedHelpers.newInstance(classWXMediaMessage);
        XposedHelpers.setObjectField(newInstance2, "mediaObject", newInstance);
        XposedHelpers.setObjectField(newInstance2, "title", title);//titleName
        if (TextUtils.isEmpty(thumbImageURL)) {
            Bitmap decodeFile = BitmapFactory.decodeResource(MainXposed.mContext.getResources(), R.drawable.ic_def);
            sendMedia(wxid, newInstance2, decodeFile);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (!TextUtils.isEmpty(thumbImageURL) && thumbImageURL.startsWith("http")) {
                        Bitmap decodeFile = null;
                        try {
                            decodeFile = Glide.with(MainXposed.mContext)
                                    .asBitmap()
                                    .load(thumbImageURL)
                                    .centerCrop()
                                    .submit(150, 150)
                                    .get();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        sendMedia(wxid, newInstance2, decodeFile);
                    }
                }
            }).start();
        }

    }
    private static void sendMedia(String wxid, Object newInstance2, Bitmap decodeFile) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decodeFile.compress(Bitmap.CompressFormat.JPEG, 85, byteArrayOutputStream);
        XposedHelpers.setObjectField(newInstance2, "thumbData", byteArrayOutputStream.toByteArray());
        Class classL = XposedHelpers.findClass("com.tencent.mm.pluginsdk.model.app.l", MainXposed.classLoader);
        Class classWxMediaMessage = XposedHelpers.findClass("com.tencent.mm.opensdk.modelmsg.WXMediaMessage", MainXposed.classLoader);
        try {
            XposedHelpers.callStaticMethod(classL, "a", new Class[]{classWxMediaMessage, String.class, String.class, String.class, int.class, String.class}, newInstance2, "", "", wxid, 4, null);
            XposedBridge.log("发送执行成功");
        } catch (Exception e) {
            XposedBridge.log("调用微信消息回复方法异常");
            XposedBridge.log(e);
        }
    }


    public static boolean sendMessage(int msgType, String roomId, String content) {
        switch (msgType) {
            case 1:
                sendText(roomId, content);
                break;
            case 3:
                ImageEnityModel imageEnityModel = JSON.parseObject(content, ImageEnityModel.class);
                if (imageEnityModel != null && !TextUtils.isEmpty(imageEnityModel.imageURL)) {
                    WXMessageUtils.sendImage(roomId, imageEnityModel.imageURL, imageEnityModel.thumbImageURL);
                } else {
                    return false;
                }
                break;
            case 6:
                WebPageEnityModel webPageEnityModel = JSON.parseObject(content, WebPageEnityModel.class);
                if (webPageEnityModel != null && !TextUtils.isEmpty(webPageEnityModel.webpageUrl)) {
                    WXMessageUtils.sendWebUrl(roomId, webPageEnityModel.title, webPageEnityModel.description, webPageEnityModel.webpageUrl, webPageEnityModel.imageUrl);
                } else {
                    return false;
                }
                break;
            case 33:
                MiniProgramPageModel miniProgramPageModel = JSON.parseObject(content, MiniProgramPageModel.class);
                if (miniProgramPageModel != null && miniProgramPageModel.miniprogrampage != null) {
                    MiniProgramPageModel.MiniprogrampageBean miniMsg = miniProgramPageModel.miniprogrampage;
                    WXMessageUtils.sendMiniMssage(roomId, miniMsg.pagepath, miniMsg.appid, miniMsg.webpageUrl, miniMsg.title,
                            miniMsg.thumb_url
                    );
                } else {
                    return false;
                }
                break;
        }
        return true;
    }

    /**
     * public String roomId;//17595315330@chatroom//群ID
     * public String nickname;//  群名昵称
     * public String memberlist;//wxid_980dllc5zh8i22;cuichenxi8895340; 群成员
     * public String displayname;//崔陈喜、崔哥//群成员昵称
     * public String roomowner;//cuichenxi8895340//群主
     * ?msg=test&userId=test&roomId=19353714656%40chatroom&group=group_wx&msgType=1
     *
     * @param paramsMap
     */
    public static void handleMessageChatInfo(final HashMap<String, String> paramsMap) {
        OkHttpUtils.post()
                .url("http://api-test-internal.gaojihealth.cn/wxgateway/api/noauth/hook/uploadGroup")
                .params(paramsMap)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        XposedBridge.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        XposedBridge.log("onResponse=" + response);
                    }
                });

    }

    public static void handleMessage(String userId, String userPhone, String nickName, String sendId, String roomId, int msgType, String msg, long createTime) {
        OkHttpUtils.get()
                .url("http://api-test-internal.gaojihealth.cn/wxgateway/api/noauth/hook/reply")
                .addParams("msg", msg)
                .addParams("userId", userId)
                .addParams("sendId", sendId)
                .addParams("roomId", roomId)
                .addParams("userPhone", userPhone)
                .addParams("nickName", nickName)
                .addParams("group", "group_wx")
                .addParams("msgType", msgType + "")
                .addParams("createTime", createTime + "")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        XposedBridge.log(e.getMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        XposedBridge.log("onResponse=" + response);
                        if (!TextUtils.isEmpty(response)) {
                            try {
                                JSONObject jsonObject = JSON.parseObject(response, JSONObject.class);
                                boolean ok = jsonObject.getBoolean("ok");
                                if (ok) {
//                                    sendText(roomId, "");
                                } else {
                                    XposedBridge.log(response);
                                }
                            } catch (Exception e) {
                                XposedBridge.log(e.getMessage());
                            }
                        }
                    }
                });

    }

}
