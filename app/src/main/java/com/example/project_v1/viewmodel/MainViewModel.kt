package com.example.project_v1.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_v1.data.repository.NetworkRepository
import com.example.project_v1.data.repository.SettingsRepository
import kotlinx.coroutines.launch

class MainViewModel(
    application: Application,
    private val repository: NetworkRepository = NetworkRepository(),
    private val settingsRepository: SettingsRepository = SettingsRepository(application)
) : AndroidViewModel(application) {
    var ipSuffix = mutableStateOf(SettingsRepository.DEFAULT_IP)
        private set
    var baseURL = mutableStateOf("http://192.168.${SettingsRepository.DEFAULT_IP}:3000/")
        private set
    var appUsername = mutableStateOf(SettingsRepository.DEFAULT_USER)
        private set
    var appFilename = mutableStateOf(SettingsRepository.DEFAULT_FILE)
        private set
    var barcodeFormats = mutableStateOf(SettingsRepository.DEFAULT_BARCODES)
        private set
    var settingDone = mutableStateOf(0)
        private set

    init {
        val saved = settingsRepository.loadSettings()
        applySettings(saved.ipSuffix, saved.username, saved.filename, saved.barcodeFormats, save = false)
    }

    fun updateSettings(ip: String, username: String, filename: String, barcodes: Set<Int>) {
        applySettings(ip, username, filename, barcodes, save = true)
    }

    private fun applySettings(ip: String, username: String, filename: String, barcodes: Set<Int>, save: Boolean) {
        ipSuffix.value = ip
        baseURL.value = "http://192.168.${ip}:3000/"
        appUsername.value = username
        appFilename.value = filename
        barcodeFormats.value = barcodes
        settingDone.value = 1
        if (save) {
            settingsRepository.saveSettings(ip, username, filename, barcodes)
        }
    }

    fun execPOST(data: String, command: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.post(baseURL.value, data, command)
            onResult(result)
        }
    }

    fun execGET(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.get(baseURL.value)
            onResult(result)
        }
    }
}
