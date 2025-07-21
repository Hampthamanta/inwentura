package com.example.project_v1.data.repository

import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode

class SettingsRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    data class Settings(
        val ipSuffix: String,
        val username: String,
        val filename: String,
        val barcodeFormats: Set<Int>
    )

    fun loadSettings(): Settings {
        val ip = prefs.getString(KEY_IP, DEFAULT_IP) ?: DEFAULT_IP
        val user = prefs.getString(KEY_USER, DEFAULT_USER) ?: DEFAULT_USER
        val file = prefs.getString(KEY_FILE, DEFAULT_FILE) ?: DEFAULT_FILE
        val formatsStr = prefs.getStringSet(KEY_BARCODES, DEFAULT_BARCODES_STR) ?: DEFAULT_BARCODES_STR
        val formats = formatsStr.mapNotNull { it.toIntOrNull() }.toSet()
        return Settings(ip, user, file, if (formats.isEmpty()) DEFAULT_BARCODES else formats)
    }

    fun saveSettings(ipSuffix: String, username: String, filename: String, barcodeFormats: Set<Int>) {
        prefs.edit()
            .putString(KEY_IP, ipSuffix)
            .putString(KEY_USER, username)
            .putString(KEY_FILE, filename)
            .putStringSet(KEY_BARCODES, barcodeFormats.map { it.toString() }.toSet())
            .apply()
    }

    companion object {
        private const val PREF_NAME = "app_settings"
        private const val KEY_IP = "ip_suffix"
        private const val KEY_USER = "username"
        private const val KEY_FILE = "filename"
        private const val KEY_BARCODES = "barcode_formats"

        val DEFAULT_BARCODES = setOf(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_DATA_MATRIX,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_PDF417,
            Barcode.FORMAT_AZTEC
        )
        private val DEFAULT_BARCODES_STR = DEFAULT_BARCODES.map { it.toString() }.toSet()

        const val DEFAULT_IP = "0.173"
        const val DEFAULT_USER = "Brak"
        const val DEFAULT_FILE = "IW2024"
    }
}
