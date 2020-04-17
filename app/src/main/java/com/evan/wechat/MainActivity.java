package com.evan.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.evan.wechat.entity.AppInfo;
import com.evan.wechat.update.UpdateManger;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.tv_xp_status)
    TextView tvXpStatus;
    @BindView(R.id.tv_wx_v)
    TextView tvwxv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 沉浸式状态栏
        View root = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        ButterKnife.bind(this, root);
        //设置view
        setContentView(root);

        //检测环境
        AppInfo.getInstance().ValidateEnvironment(this);
        //初始化QMUIGroupListView
        initMainContentView();
        UpdateManger.check(this);
    }


    //初始化QMUIGroupListView
    private void initMainContentView() {
        AppInfo appInfo = AppInfo.getInstance();
        tvXpStatus.setText(appInfo.isXposedActive() ? "已激活" : "未激活");
        tvwxv.setText(appInfo.isWechatInstall() ? "V" + appInfo.getWechatVersionName() : "未安装");
//        boolean boolResult;
//        String strResult;
        //region 系统版本与root状态
//        //系统版本
//        QMUICommonListItemView listItemSystem = mGroupListView.createItemView("系统版本");
//        listItemSystem.setDetailText("Android V" + appInfo.getAndroidVersionName());
//        boolResult = appInfo.isSupportAndroid();
//        listItemSystem.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
//        //是否已ROOT
//        QMUICommonListItemView listItemRoot = mGroupListView.createItemView("是否ROOT");
//        boolResult = appInfo.isDeviceRooted();
//        strResult = boolResult ? "已ROOT" : "未ROOT";
//        listItemRoot.setDetailText(strResult);
//        listItemRoot.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
//        QMUIGroupListView.newSection(this)
//                .setTitle("系统状态")
//                .addItemView(listItemSystem, null)
//                .addItemView(listItemRoot, null)
//                .addTo(mGroupListView);
//        //endregion
//
//        //region 微信版本状态
//        //微信安装版本
//        QMUICommonListItemView listItemWechatVersion = mGroupListView.createItemView("微信版本");
//        boolResult = appInfo.isWechatInstall();
//        listItemWechatVersion.setDetailText(boolResult ? "V" + appInfo.getWechatVersionName() : "未安装");
//        boolResult = appInfo.isSupportWechat();
//        listItemWechatVersion.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
//        QMUIGroupListView.newSection(this)
//                .setTitle("微信状态")
//                .addItemView(listItemWechatVersion, null)
//                .addTo(mGroupListView);
//        //endregion
//
//        //region Xposed框架状态
//        //Xposed版本
//        QMUICommonListItemView listItemXposed = mGroupListView.createItemView("Xposed版本");
//        boolResult = appInfo.isXposedInstall();
//        listItemXposed.setDetailText(boolResult ? "V" + appInfo.getXposedVersionName() : "未安装");
//        listItemXposed.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
//        //Xposed模块激活
//        QMUICommonListItemView listItemXposedActive = mGroupListView.createItemView("Xposed模块激活");
//        boolResult = appInfo.isXposedActive();
//        listItemXposedActive.setDetailText(boolResult ? "已激活" : "未激活");
//        listItemXposedActive.setImageDrawable(ContextCompat.getDrawable(this, boolResult ? R.drawable.qmui_icon_checkbox_checked : R.mipmap.icon_error));
//        QMUIGroupListView.newSection(this)
//                .setTitle("Xposed框架状态")
//                .addItemView(listItemXposed, null)
//                .addItemView(listItemXposedActive, null)
//                .addTo(mGroupListView);
        //endregion

    }
}
