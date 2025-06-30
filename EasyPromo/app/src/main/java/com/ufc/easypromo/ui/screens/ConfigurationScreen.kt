package com.ufc.easypromo.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ufc.easypromo.data.PreferencesDataStore
import com.ufc.easypromo.util.NotificationHelper
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigurationScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val preferencesDataStore = remember { PreferencesDataStore(context) }
    val scope = rememberCoroutineScope()
    val priceDropEnabled by preferencesDataStore.priceDropEnabled.collectAsState(initial = false)

    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurações") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Tema do aplicativo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Claro")
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = {
                        scope.launch { preferencesDataStore.setDarkThemeEnabled(it) }
                        onThemeChange(it)
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Text("Escuro")
            }
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Notificações de desconto em favoritos")
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = priceDropEnabled,
                    onCheckedChange = {
                        scope.launch { preferencesDataStore.setPriceDropEnabled(it) }
                    }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    scope.launch { preferencesDataStore.clearFavorites() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Limpar Favoritos")
            }
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { showTimePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar Notificação (Escolher Horário)")
            }

            if (showTimePicker) {
                val contextDialog = LocalContext.current
                val now = Calendar.getInstance()
                // Use LaunchedEffect to show the dialog only once per state change
                LaunchedEffect(showTimePicker) {
                    TimePickerDialog(
                        contextDialog,
                        { _, hour: Int, minute: Int ->
                            showTimePicker = false
                            val scheduled = Calendar.getInstance().apply {
                                set(Calendar.HOUR_OF_DAY, hour)
                                set(Calendar.MINUTE, minute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                                if (before(Calendar.getInstance())) {
                                    add(Calendar.DAY_OF_MONTH, 1)
                                }
                            }
                            NotificationHelper.scheduleCustomNotification(
                                context = contextDialog,
                                title = "Notificação Agendada",
                                message = "Esta notificação foi agendada para ${"%02d".format(hour)}:${"%02d".format(minute)}.",
                                triggerAtMillis = scheduled.timeInMillis
                            )
                        },
                        now.get(Calendar.HOUR_OF_DAY),
                        now.get(Calendar.MINUTE),
                        true
                    ).apply {
                        setOnCancelListener { showTimePicker = false }
                    }.show()
                }
            }
        }
    }
}