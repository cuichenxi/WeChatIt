
package com.evan.wechat;

import android.app.Application;

import com.evan.wechat.update.UpdateManger;

public class BaseApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        UpdateManger.init(this);
    }
}
