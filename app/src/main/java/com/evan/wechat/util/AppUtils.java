/*
 * ************************************************************
 * 文件：AppUtils.java  模块：app  项目：WeChatGenius
 * 当前修改时间：2018年08月20日 11:37:49
 * 上次修改时间：2018年08月20日 11:37:49
 * 作者：大路
 * Copyright (c) 2018
 * ************************************************************
 */

package com.evan.wechat.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

public final class AppUtils {
    //安装包
    public static final String PACKAGE_NAME_WECHAT = "com.tencent.mm";
    public static final String PACKAGE_NAME_XPOSED = "de.robv.android.xposed.installer";

    //获取当前APP的版本
    public static String getVersionName(Context context) {
        //PackageManager 可以获取清单文件中的所有信息
        PackageManager manager = context.getPackageManager();
        try {
            //getPackageName()获取到当前程序的包名
            PackageInfo packageInfo = manager.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    //获取当前APP的versionCode
    public static int getVersionCode(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            return manager.getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    //获取APP的安装版本号。未安装，返回空字符串。
    public static String getAppVersionName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(packageName)) {
                return packageInfo.versionName;
            }
        }

        return "";
    }

    //Xposed模块是否激活。默认返回false；激活后hook将其值改为true
    public static boolean isModuleActive() {
        return false;
    }

    private static void printMessage(final InputStream input) {
        new Thread(new Runnable() {
            public void run() {
                final Reader reader = new InputStreamReader(input);
                final BufferedReader bf = new BufferedReader(reader);
                String line = null;
                try {
                    while ((line = bf.readLine()) != null) {
                    }
                    input.close();
                    reader.close();
                    bf.close();
                } catch (Exception e) {
                    try {
                        input.close();
                        reader.close();
                        bf.close();
                    } catch (Exception e1) {e1.printStackTrace();}
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 执行shell命令 有返回值
     *
     * @param strCmd
     * @return
     */
    public static String rtdoCmd(String strCmd) {
        try {
            Process process = null;
            PrintWriter printWriter = null;
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println(strCmd);
            printWriter.flush();
            printWriter.close();
            process.waitFor();
            InputStream inputStream = null;
            String ret = "";
            try {
                inputStream = process.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    ret += line;
                }
//                printMessage(process.getErrorStream());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 执行shell命令  无返回值
     *
     * @param strCmd
     * @return
     */
    public static String doCmd(String strCmd) {
        Process process = null;
        try {
            PrintWriter printWriter = null;
            process = Runtime.getRuntime().exec("su");
            printWriter = new PrintWriter(process.getOutputStream());
            printWriter.println(strCmd);
            printWriter.flush();
            printWriter.close();
            printMessage(process.getInputStream());
            printMessage(process.getErrorStream());
            process.waitFor();
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
