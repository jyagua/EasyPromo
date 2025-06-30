package com.ufc.easypromo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.ufc.easypromo.ui.navigation.AppNavHost
import com.ufc.easypromo.ui.theme.EasyPromoTheme
import com.ufc.easypromo.util.AlarmHelper
import com.ufc.easypromo.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Schedule repeating alarm for price drop notifications
        AlarmHelper.schedulePriceDropAlarm(this)

        // Check price drops on launch
        mainViewModel.checkPriceDropsAndNotify()

        setContent {
            val darkThemePref = mainViewModel.darkThemeEnabled.collectAsState(initial = true).value
            EasyPromoTheme(darkTheme = darkThemePref) {
                AppNavHost(
                    isDarkTheme = darkThemePref,
                    onThemeChange = { mainViewModel.setDarkThemeEnabled(it) }
                )
            }
        }
    }
}