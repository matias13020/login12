package com.matias.login.ui.iot

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.matias.login.data.SensorDevice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoTControlScreen(
    viewModel: IoTViewModel,
    onLogout: () -> Unit
) {
    val sensors by viewModel.sensors.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadSensors()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Control IoT - TI3042") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Salir")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Dispositivos IoT Conectados (WiFi / MQTT)",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (sensors.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sensors) { device ->
                        SensorCard(device = device, onToggle = {
                            viewModel.toggleDevice(device.id, device.status)
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun SensorCard(device: SensorDevice, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = device.name,
                    fontSize = 16.sp,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Valor / Telemetría: ${device.value}",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (device.status) "Estado: ACTIVO" else "Estado: INACTIVO",
                    fontSize = 12.sp,
                    color = if (device.status) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            Switch(
                checked = device.status,
                onCheckedChange = { onToggle() }
            )
        }
    }
}
