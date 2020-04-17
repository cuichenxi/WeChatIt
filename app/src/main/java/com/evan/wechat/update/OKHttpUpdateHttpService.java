///*
// * ************************************************************
// * 文件：OKHttpUpdateHttpService.java  模块：app  项目：WeChatGenius
// * 当前修改时间：2020年04月15日 12:56:28
// * 上次修改时间：2020年02月12日 16:08:50
// * 作者：大路
// * Copyright (c) 2020
// * ************************************************************
// */
//
//package com.evan.wechat.update;
//
//import androidx.annotation.NonNull;
//
//import com.xuexiang.xupdate.proxy.IUpdateHttpService;
//import com.zhy.http.okhttp.OkHttpUtils;
//import com.zhy.http.okhttp.callback.FileCallBack;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import java.io.File;
//import java.util.Map;
//import java.util.TreeMap;
//
//import okhttp3.Call;
//
//public class OKHttpUpdateHttpService implements IUpdateHttpService {
//
//    public OKHttpUpdateHttpService() {
//    }
//
//    @Override
//    public void asyncGet(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
//        OkHttpUtils.<String>get()
//                .url(url)
//                .params(transform(params))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//                        callBack.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        callBack.onSuccess(response.body().toString());
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        callBack.onError(response.getException());
//                    }
//
//                });
//    }
//
//    @Override
//    public void asyncPost(@NonNull String url, @NonNull Map<String, Object> params, final @NonNull Callback callBack) {
//        GHttp.<String>post(url)
//                .execute(new JsonCallback<String>() {
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        callBack.onSuccess(response.body().toString());
//                    }
//
//                    @Override
//                    public void onError(Response<String> response) {
//                        super.onError(response);
//                        callBack.onError(response.getException());
//                    }
//                });
//    }
//
//    @Override
//    public void download(@NonNull String url, @NonNull String path, @NonNull String fileName, final @NonNull DownloadCallback callback) {
////        GHttp.<File>get(url)
////                .execute(new FileCallback(path, fileName) {
////                    @Override
////                    public void onSuccess(Response<File> response) {
////                        callback.onSuccess(response.body());
////                    }
////
////                    @Override
////                    public void downloadProgress(Progress progress) {
////                        long totalSize = progress.totalSize;
////                        callback.onProgress(progress.progress, totalSize);
////                    }
////
////                    @Override
////                    public void onError(Response<File> response) {
////                        super.onError(response);
////                        callback.onError(response.getException());
////                    }
////
////                    @Override
////                    public void onStart(Request<File, ? extends Request> request) {
////                        super.onStart(request);
////                        callback.onStart();
////                    }
////
////                });
//        //ssl 验证不通过
//        OkHttpUtils.get()
//                .url(url)
//                .build()
//                .execute(new FileCallBack(path, fileName) {
//                    @Override
//                    public void inProgress(float progress, long total, int id) {
//                        callback.onProgress(progress, total);
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//                        callback.onError(e);
//                    }
//
//                    @Override
//                    public void onResponse(File response, int id) {
//                        callback.onSuccess(response);
//                    }
//
//                    @Override
//                    public void onBefore(okhttp3.Request request, int id) {
//                        super.onBefore(request, id);
//                        callback.onStart();
//                    }
//
//                });
//    }
//
//    @Override
//    public void cancelDownload(@NonNull String url) {
//
//    }
//
//    private Map<String, String> transform(Map<String, Object> params) {
//        Map<String, String> map = new TreeMap<>();
//        for (Map.Entry<String, Object> entry : params.entrySet()) {
//            map.put(entry.getKey(), entry.getValue().toString());
//        }
//        return map;
//    }
//
//
//}
