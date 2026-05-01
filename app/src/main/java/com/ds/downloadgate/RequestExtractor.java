package com.ds.downloadgate;

import android.content.Context;
import android.net.Uri;

import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Field;

/**
 * Helper reflection untuk mengekstrak data dari
 * android.app.DownloadManager dan DownloadManager.Request.
 *
 * Field names berdasarkan AOSP Android 14/15:
 * frameworks/base/core/java/android/app/DownloadManager.java
 *
 * Selalu dump field jika field utama tidak ditemukan
 * karena HyperOS CN bisa rename internal fields.
 */
public class RequestExtractor {

    private static final String TAG = "[DownloadGate]";

    /**
     * Ekstrak Uri dari DownloadManager.Request.
     * Field AOSP: mUri
     */
    public static Uri extractUri(Object request) {
        try {
            Field f = findField(request.getClass(), "mUri");
            if (f != null) {
                f.setAccessible(true);
                return (Uri) f.get(request);
            }
        } catch (Exception e) {
            XposedBridge.log(TAG + " extractUri error: " + e.getMessage());
        }
        dumpFields(request, "Request");
        return null;
    }

    /**
     * Ekstrak Context dari DownloadManager instance.
     * Field AOSP: mContext
     */
    public static Context extractContext(Object downloadManager) {
        try {
            Field f = findField(downloadManager.getClass(), "mContext");
            if (f != null) {
                f.setAccessible(true);
                return (Context) f.get(downloadManager);
            }
        } catch (Exception e) {
            XposedBridge.log(TAG + " extractContext error: " + e.getMessage());
        }
        return null;
    }

    /**
     * Cari field di class hierarchy (termasuk superclass).
     */
    private static Field findField(Class<?> clazz, String name) {
        Class<?> c = clazz;
        while (c != null && c != Object.class) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException e) {
                c = c.getSuperclass();
            }
        }
        return null;
    }

    /**
     * Dump semua field untuk debugging di logcat.
     * Panggil saat field utama tidak ditemukan.
     */
    public static void dumpFields(Object obj, String label) {
        XposedBridge.log(TAG + " --- Field dump: " + label + " ---");
        Class<?> c = obj.getClass();
        while (c != null && c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                XposedBridge.log(TAG + "  " + f.getType().getSimpleName() + " " + f.getName());
            }
            c = c.getSuperclass();
        }
    }
}
