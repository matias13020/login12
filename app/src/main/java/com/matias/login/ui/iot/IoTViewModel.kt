package com.matias.login.ui.iot

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.matias.login.data.DatabaseHelper
import com.matias.login.data.SensorDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class IoTViewModel(application: Application) : AndroidViewModel(application) {
    private val dbHelper = DatabaseHelper(application)

    private val _sensors = MutableStateFlow<List<SensorDevice>>(emptyList())
    val sensors: StateFlow<List<SensorDevice>> = _sensors.asStateFlow()

    init {
        loadSensors()
    }

    fun loadSensors() {
        viewModelScope.launch {
            _sensors.value = dbHelper.getAllSensors()
        }
    }

    fun toggleDevice(id: Int, currentStatus: Boolean) {
        viewModelScope.launch {
            dbHelper.updateSensorStatus(id, !currentStatus)
            loadSensors()
        }
    }
}
