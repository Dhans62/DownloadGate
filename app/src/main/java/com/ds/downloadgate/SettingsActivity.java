package com.ds.downloadgate;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity utama — UI settings modul.
 * Layout: activity_settings.xml
 * TODO: Implementasi penuh di fase UI build.
 *
 * Yang akan ada:
 * - Toggle module on/off
 * - Pilih target app (query PackageManager untuk ACTION_VIEW https)
 * - RecyclerView daftar app + toggle whitelist
 * - RecyclerView intercept log
 * - Tombol test intercept
 * - Dark/Light theme switch
 */
public class SettingsActivity extends AppCompatActivity {

    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_settings); // Aktifkan saat layout selesai
        prefManager = new PrefManager(this);
    }
}
