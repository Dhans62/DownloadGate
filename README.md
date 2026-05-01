# DownloadGate

LSPosed module yang mengalihkan `DownloadManager.enqueue()` ke aplikasi download pihak ketiga. Dibuat sebagai solusi untuk masalah download yang gagal pada ROM HyperOS CN 3 Portingan (Android 15).

---

## Latar Belakang Masalah

ROM HyperOS CN 3 yang di-port ke perangkat global membawa `com.android.providers.downloads` versi Cina yang tidak kompatibel dengan endpoint API global. Gejala yang muncul:

- Download stuck di status **Pending** tanpa progress
- Download langsung berstatus **Failed** tanpa error message yang jelas
- File yang diunduh berukuran **0 KB**
- Masalah terjadi spesifik pada URL yang membutuhkan **session/cookie/auth header** — seperti endpoint API, CDN dengan token, atau server yang memvalidasi User-Agent

Penyebab teknis: `DownloadProvider` pada ROM CN tidak mampu meneruskan HTTP header dari request dengan benar, sehingga server menolak koneksi atau mengembalikan response kosong.

---

## Solusi

Modul ini melakukan hooking pada `android.app.DownloadManager.enqueue()` menggunakan LSPosed API. Setiap kali aplikasi yang ada di whitelist memanggil method tersebut, modul:

1. Mengekstrak URI dari objek `Request`
2. Membatalkan proses `enqueue` ke `DownloadProvider` sistem
3. Mengirim `Intent.ACTION_VIEW` ke aplikasi download pilihan user

Dengan cara ini, `com.android.providers.downloads` tidak pernah menyentuh request tersebut.

---

## Persyaratan

| Komponen | Versi |
|---|---|
| Android | 12 ke atas |
| LSPosed | Framework aktif (Zygisk/Riru) |
| Magisk | 24.0 ke atas |
| Target ROM | HyperOS CN 3 Port atau ROM lain dengan masalah serupa |

---

## Instalasi

1. Pastikan **LSPosed** sudah terinstal dan aktif
2. Download APK dari halaman [Releases](../../releases)
3. Install APK secara manual
4. Buka aplikasi **DownloadGate**
5. Aktifkan module toggle
6. Pilih aplikasi tujuan redirect (Chrome, ADM, IDM Lite, dll)
7. Tambahkan aplikasi yang ingin di-intercept ke whitelist
8. Aktifkan scope modul di **LSPosed Manager** untuk aplikasi tersebut
9. Force stop atau reboot aplikasi target

---

## Cara Kerja

```
Aplikasi (misal: Claude)
    |
    v
DownloadManager.enqueue(Request)   <-- Hook dipasang di sini
    |
    +-- [Tanpa modul] --> com.android.providers.downloads
    |                              (gagal di HyperOS CN)
    |
    +-- [Dengan modul aktif]
            |
            v
        Ekstrak URI dari Request
        Batalkan enqueue (return -1)
            |
            v
        Intent.ACTION_VIEW (Chooser)
            |
            v
        Browser / ADM / App pilihan user
```

---

## Konfigurasi

Semua pengaturan dilakukan lewat UI aplikasi tanpa perlu edit file konfigurasi manual.

**Module Toggle** — aktifkan atau nonaktifkan modul secara keseluruhan tanpa harus uninstall.

**Redirect Target** — pilih satu aplikasi tujuan dari daftar aplikasi yang terinstal. Aplikasi yang muncul adalah yang memiliki kemampuan handle URL (ACTION_VIEW untuk scheme https).

**App Whitelist** — pilih aplikasi mana saja yang download-nya akan di-intercept. Aplikasi yang tidak ada di whitelist tetap menggunakan DownloadManager sistem secara normal.

**Intercept Log** — riwayat intercept terakhir beserta URI dan status redirect.

---

## Build dari Source

### Menggunakan GitHub Actions (direkomendasikan)

Setiap push ke branch `main` akan otomatis menghasilkan APK debug yang bisa didownload dari tab **Actions**.

Untuk membuat release resmi, buat tag dengan format `v*`:

```bash
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions akan build, sign, dan upload APK ke halaman Releases secara otomatis.

### Setup Signing (untuk release)

Tambahkan secrets berikut di repository Settings > Secrets:

| Secret | Isi |
|---|---|
| `SIGNING_KEY` | Keystore dalam format base64 |
| `KEY_ALIAS` | Alias key di keystore |
| `KEY_STORE_PASSWORD` | Password keystore |
| `KEY_PASSWORD` | Password key |

Generate keystore baru jika belum punya:

```bash
keytool -genkey -v -keystore downloadgate.jks \
  -alias downloadgate -keyalg RSA -keysize 2048 \
  -validity 10000
```

Encode ke base64 untuk dimasukkan ke Secrets:

```bash
base64 -w 0 downloadgate.jks
```

---

## Catatan Teknis

Modul ini menggunakan reflection untuk mengakses field internal `DownloadManager.Request` (`mUri`, `mContext`). Nama field diambil dari AOSP source Android 14/15. Jika ROM yang digunakan memodifikasi nama field tersebut, modul akan otomatis melakukan dump field ke logcat untuk memudahkan debugging.

Periksa logcat dengan filter `[DownloadGate]` untuk melihat output modul:

```bash
adb logcat | grep "\[DownloadGate\]"
```

---

## Lisensi

MIT License — bebas digunakan, dimodifikasi, dan didistribusikan.
