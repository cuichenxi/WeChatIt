package com.virjar.sekiro.log;

import android.util.Log;

import org.slf4j.LoggerFactory;

import de.robv.android.xposed.XposedBridge;

public class SekiroLogger {
    public static String tag = "Sekiro";


    public static void info(String msg) {
        XposedBridge.log("Sekiro=" + msg);
        Log.i(tag, msg);
    }

    public static void info(String msg, Throwable throwable) {
        Log.i(tag, msg, throwable);
        XposedBridge.log("Sekiro=" + msg);
    }

    public static void warn(String msg) {
        Log.w(tag, msg);
        XposedBridge.log("Sekiro=" + msg);
    }

    public static void warn(String msg, Throwable throwable) {
        XposedBridge.log("Sekiro=" + msg);
        Log.w(tag, msg, throwable);
    }

    public static void error(String msg) {
        Log.e(tag, msg);
        XposedBridge.log("Sekiro=" + msg);
    }

    public static void error(String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
        XposedBridge.log("Sekiro=" + msg);
    }
}
