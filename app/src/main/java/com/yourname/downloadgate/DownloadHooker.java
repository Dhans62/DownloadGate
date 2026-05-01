package com.yourname.downloadgate;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Core hook — intercept DownloadManager.enqueue() dan
 * redirect ke app tujuan yang dipilih user via UI.
 */
public class DownloadHooker {

    private static final String TAG = "[DownloadGate]";

    public static void hook(XC_LoadPackage.LoadPackageParam lpparam,
                            de.robv.android.xposed.XSharedPreferences xprefs) {
        try {
            Class<?> dmClass = XposedHelpers.findClass(
                "android.app.DownloadManager",
                lpparam.classLoader
            );
            Class<?> requestClass = XposedHelpers.findClass(
                "android.app.DownloadManager$Request",
                lpparam.classLoader
            );

            XposedBridge.hookMethod(
                dmClass.getMethod("enqueue", requestClass),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                        // Reload prefs setiap intercept agar perubahan UI langsung efektif
                        xprefs.reload();

                        boolean moduleEnabled = xprefs.getBoolean(
                            PrefManager.KEY_MODULE_ENABLED, false);
                        if (!moduleEnabled) return;

                        String targetPkg = xprefs.getString(
                            PrefManager.KEY_TARGET_APP, "");

                        Object request = param.args[0];
                        Context context = RequestExtractor.extractContext(param.thisObject);
                        Uri uri = RequestExtractor.extractUri(request);

                        if (context == null) {
                            XposedBridge.log(TAG + " Context null — skipping");
                            return;
                        }
                        if (uri == null) {
                            XposedBridge.log(TAG + " URI null — skipping");
                            return;
                        }

                        XposedBridge.log(TAG + " Intercepted: " + uri + " from " + lpparam.packageName);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        if (!TextUtils.isEmpty(targetPkg)) {
                            // User sudah pilih app spesifik — langsung launch
                            intent.setPackage(targetPkg);
                        }

                        // Chooser — Android handle "Selalu/Sekali" secara native
                        Intent chooser = Intent.createChooser(intent, null);
                        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        try {
                            context.startActivity(chooser);
                            // Batalkan enqueue asli
                            param.setResult(-1L);
                        } catch (Exception e) {
                            XposedBridge.log(TAG + " startActivity failed: " + e.getMessage());
                            // Biarkan enqueue asli berjalan jika redirect gagal
                        }
                    }
                }
            );

            XposedBridge.log(TAG + " Hook terpasang: " + lpparam.packageName);

        } catch (NoSuchMethodException e) {
            XposedBridge.log(TAG + " enqueue() tidak ditemukan: " + e.getMessage());
        } catch (Throwable t) {
            XposedBridge.log(TAG + " Hook error: " + t.getMessage());
        }
    }
}
