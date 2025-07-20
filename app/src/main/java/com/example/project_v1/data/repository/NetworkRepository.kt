package com.example.project_v1.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.io.IOException

class NetworkRepository {
    private val client = OkHttpClient()

    suspend fun post(baseUrl: String, data: String, command: String): String = withContext(Dispatchers.IO) {
        val mediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = data.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(baseUrl + command)
            .post(requestBody)
            .build()
        return@withContext try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body: ResponseBody? = response.body
                body?.string() ?: "Empty response"
            } else {
                "Error: ${'$'}{response.code}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Network error"
        }
    }

    suspend fun get(baseUrl: String, command: String = "test"): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(baseUrl + command)
            .build()
        return@withContext try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val body: ResponseBody? = response.body
                body?.string() ?: "Empty response"
            } else {
                "Error: ${'$'}{response.code}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Network error"
        }
    }
}
