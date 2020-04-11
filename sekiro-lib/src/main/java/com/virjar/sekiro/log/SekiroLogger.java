package com.virjar.sekiro.log;

import android.util.Log;

import org.slf4j.LoggerFactory;

public class SekiroLogger {
    public static String tag = "Sekiro";


    public static void info(String msg) {
        Log.i(tag, msg);
    }

    public static void info(String msg, Throwable throwable) {
        Log.i(tag, msg, throwable);
    }

    public static void warn(String msg) {
        Log.w(tag, msg);
    }

    public static void warn(String msg, Throwable throwable) {
        Log.w(tag, msg, throwable);
    }

    public static void error(String msg) {
        Log.e(tag, msg);
    }

    public static void error(String msg, Throwable throwable) {
        Log.e(tag, msg, throwable);
    }
}
