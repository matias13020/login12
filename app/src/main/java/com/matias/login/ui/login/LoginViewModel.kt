package com.matias.login.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // URL base de tu API en la Raspberry Pi (cambia la IP si es necesario)
    private val baseUrl = "http://192.168.18.55"

    private val _loginResult = MutableStateFlow<Boolean?>(null)
    val loginResult: StateFlow<Boolean?> = _loginResult.asStateFlow()

    private val _registerResult = MutableStateFlow<Boolean?>(null)
    val registerResult: StateFlow<Boolean?> = _registerResult.asStateFlow()

    fun login(user: String, pass: String) {
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                sendPostRequest("$baseUrl/login.php", user, pass)
            }
            _loginResult.value = success
        }
    }

    fun register(user: String, pass: String) {
        if (user.isBlank() || pass.isBlank()) {
            _registerResult.value = false
            return
        }
        viewModelScope.launch {
            val success = withContext(Dispatchers.IO) {
                sendPostRequest("$baseUrl/register.php", user, pass)
            }
            _registerResult.value = success
        }
    }

    private fun sendPostRequest(urlString: String, user: String, pass: String): Boolean {
        return try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val jsonInput = JSONObject().apply {
                put("username", user)
                put("password", pass)
            }

            conn.outputStream.use { os ->
                val input = jsonInput.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            val responseCode = conn.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseString = conn.inputStream.bufferedReader().use { it.readText() }
                val jsonResponse = JSONObject(responseString)
                jsonResponse.optBoolean("success", false)
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun resetState() {
        _loginResult.value = null
        _registerResult.value = null
    }
}
