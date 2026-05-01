package com.ds.downloadgate;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper SharedPreferences — MODE_WORLD_READABLE agar bisa dibaca
 * dari dalam proses hook (konteks app lain via XSharedPreferences).
 *
 * CATATAN: MODE_WORLD_READABLE deprecated di API 23+ tapi masih
 * berfungsi di lingkungan LSPosed via XSharedPreferences.
 */
public class PrefManager {

    public static final String PREF_NAME          = "downloadgate_prefs";
    public static final String KEY_MODULE_ENABLED = "module_enabled";
    public static final String KEY_TARGET_APP     = "target_app_package";
    public static final String KEY_WHITELIST      = "app_whitelist";

    // Default: kosong = semua app di-intercept jika modul aktif
    public static final String DEFAULT_TARGET     = "";

    private final SharedPreferences prefs;

    @SuppressWarnings("deprecation")
    public PrefManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_WORLD_READABLE);
    }

    public boolean isModuleEnabled() {
        return prefs.getBoolean(KEY_MODULE_ENABLED, false);
    }

    public void setModuleEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_MODULE_ENABLED, enabled).apply();
    }

    public String getTargetApp() {
        return prefs.getString(KEY_TARGET_APP, DEFAULT_TARGET);
    }

    public void setTargetApp(String packageName) {
        prefs.edit().putString(KEY_TARGET_APP, packageName).apply();
    }

    public Set<String> getWhitelist() {
        return prefs.getStringSet(KEY_WHITELIST, new HashSet<>());
    }

    public void setWhitelist(Set<String> packages) {
        prefs.edit().putStringSet(KEY_WHITELIST, packages).apply();
    }

    public boolean isWhitelisted(String packageName) {
        return getWhitelist().contains(packageName);
    }
}
