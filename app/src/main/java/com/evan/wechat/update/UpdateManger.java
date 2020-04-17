/*
 * ************************************************************
 * 文件：UpdateManger.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2020年04月15日 12:56:28
 * 上次修改时间：2020年02月12日 16:00:57
 * 作者：大路
 * Copyright (c) 2020
 * ************************************************************
 */

package com.evan.wechat.update;


import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.evan.wechat.BuildConfig;
import com.evan.wechat.constant.Constants;
import com.evan.wechat.constant.UpdateConstant;
import com.evan.wechat.util.DeviceUtil;
import com.evan.wechat.util.FileUtils;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.PromptEntity;
import com.xuexiang.xupdate.entity.UpdateEntity;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.proxy.IUpdateParser;
import com.xuexiang.xupdate.proxy.IUpdatePrompter;
import com.xuexiang.xupdate.proxy.IUpdateProxy;
import com.xuexiang.xupdate.service.OnFileDownloadListener;
import com.xuexiang.xupdate.utils.UpdateUtils;
import com.xuexiang.xupdate.widget.UpdateDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;

import external.com.alibaba.fastjson.JSON;
import okhttp3.Call;

public class UpdateManger {
    private static final String TAG = "UpdateManger";


    public static void init(Application application) {
        XUpdate.get()
                .debug(BuildConfig.DEBUG)      //开启debug模式，可用于问题的排查
                .isWifiOnly(false)     //默认设置只在wifi下检查版本更新
                .isGet(true)          //默认设置使用get请求检查版本
                .isAutoMode(false)    //默认设置非自动模式，可根据具体使用配置
                .setApkCacheDir(FileUtils.getFilesDir(application, Constants.APP_CACHE_APK).getPath())
                .param("versionCode", UpdateUtils.getVersionCode(application)) //设置默认公共请求参数
                .setOnUpdateFailureListener(new OnUpdateFailureListener() { //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {

                    }
                })
                .setIUpdateParser(new CustomUpdateParser())
                .setIUpdateHttpService(new OKHttpUpdateHttpService1()) //这个必须设置！实现网络请求功能。
                .init(application);   //这个必须初始化

    }

    public static class CustomUpdateParser implements IUpdateParser {
        @Override
        public UpdateEntity parseJson(String json) throws Exception {
            UpdateResult result = JSON.parseObject(json, UpdateResult.class);
            if (result != null && result.content != null) {
                UpdateResult.ContentBean content = result.content;
                return new UpdateEntity()
                        .setHasUpdate(content.hasUpdate())
                        .setIsIgnorable(!content.forceUpgrade)
                        .setVersionCode(content.versionCode)
                        .setForce(content.forceUpgrade)
                        .setShowNotification(true)
                        .setVersionName(content.versionName)
                        .setUpdateContent(content.upgradeMsg)
                        .setMd5(content.MD5)
                        .setSizeWithMB(content.appSize)
                        .setDownloadUrl(content.downloadUrl);

            }
            return null;
        }
    }

    private static UpdateEntity mUpdateEntity;

    public static void check(Activity activity) {
        int versionCode = DeviceUtil.getVersion(activity);
        String versionName = DeviceUtil.getVersionName(activity);
        XUpdate.newBuild(activity)
                .param("appId", UpdateConstant.APP_ID)
                .param("appType", 1)
                .param("versionCode", versionCode)
                .param("versionName", versionName)
                .updatePrompter(new IUpdatePrompter() {
                    @Override
                    public void showPrompt(@NonNull UpdateEntity updateEntity, @NonNull IUpdateProxy updateProxy, @NonNull PromptEntity promptEntity) {
                        mUpdateEntity = updateEntity;
                        promptEntity.setSupportBackgroundUpdate(!updateEntity.isForce());
                        UpdateDialog.newInstance(updateEntity, updateProxy, promptEntity).setOnFileDownloadListener(new OnFileDownloadListener() {
                            @Override
                            public void onStart() {

                            }

                            @Override
                            public void onProgress(float progress, long total) {

                            }

                            @Override
                            public boolean onCompleted(File file) {
                                if (mUpdateEntity != null) {
                                    //统计下载次数
                                    OkHttpUtils.get()
                                            .url(UpdateConstant.URL + "/upgrade/feedBack")
                                            .addParams("appId", UpdateConstant.APP_ID)
                                            .addParams("appType", "1")
                                            .addParams("versionCode", mUpdateEntity.getVersionCode() + "")
                                            .build()
                                            .execute(new StringCallback() {
                                                @Override
                                                public void onError(Call call, Exception e, int id) {

                                                }

                                                @Override
                                                public void onResponse(String response, int id) {

                                                }
                                            });
                                }
                                return false;
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        }).show();
                    }
                })
                .updateUrl(UpdateConstant.URL + "/upgrade/getUpgradeInfo")
                .update();
    }
}
