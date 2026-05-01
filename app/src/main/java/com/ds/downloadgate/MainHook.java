package com.ds.downloadgate;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.util.Set;

/**
 * Entry point LSPosed.
 * Didaftarkan di assets/xposed_init.
 *
 * Scope: semua package — filter dilakukan via whitelist
 * yang disimpan di SharedPreferences dan dibaca via XSharedPreferences.
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG    = "[DownloadGate]";
    private static final String SELF   = "com.ds.downloadgate";

    private XSharedPreferences xprefs;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) {
        // Jangan hook diri sendiri
        if (lpparam.packageName.equals(SELF)) return;

        // Inisialisasi XSharedPreferences sekali
        if (xprefs == null) {
            xprefs = new XSharedPreferences(SELF, PrefManager.PREF_NAME);
            xprefs.makeWorldReadable();
        }

        xprefs.reload();

        boolean moduleEnabled = xprefs.getBoolean(PrefManager.KEY_MODULE_ENABLED, false);
        if (!moduleEnabled) return;

        // Cek whitelist — jika whitelist kosong, skip semua
        Set<String> whitelist = xprefs.getStringSet(PrefManager.KEY_WHITELIST, null);
        if (whitelist == null || whitelist.isEmpty()) {
            XposedBridge.log(TAG + " Whitelist kosong — tidak ada app yang di-hook");
            return;
        }

        if (!whitelist.contains(lpparam.packageName)) return;

        XposedBridge.log(TAG + " Loading hook untuk: " + lpparam.packageName);
        DownloadHooker.hook(lpparam, xprefs);
    }
}
