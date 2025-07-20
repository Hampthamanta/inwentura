package com.example.project_v1.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project_v1.data.repository.NetworkRepository
import kotlinx.coroutines.launch

class MainViewModel(private val repository: NetworkRepository = NetworkRepository()) : ViewModel() {
    var baseURL = mutableStateOf("http://192.168.0.173:3000/")
        private set
    var appUsername = mutableStateOf("Brak nazwy użytkownika")
        private set
    var appFilename = mutableStateOf("Brak aktywnej inwentury")
        private set
    var settingDone = mutableStateOf(0)
        private set

    fun updateSettings(ip: String, username: String, filename: String) {
        baseURL.value = "http://192.168.${ip}:3000/"
        appUsername.value = username
        appFilename.value = filename
        settingDone.value = 1
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
