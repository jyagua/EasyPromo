package com.ufc.easypromo

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModelProvider
import com.ufc.easypromo.ui.navigation.AppNavHost
import com.ufc.easypromo.ui.theme.EasyPromoTheme
import com.ufc.easypromo.util.NotificationHelper
import com.ufc.easypromo.viewmodel.MainViewModel
import com.ufc.easypromo.viewmodel.ProductViewModel

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

        NotificationHelper.schedulePriceDropAlarm(this)
        mainViewModel.checkPriceDropsAndNotify()
        val productViewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        setContent {
            val darkThemePref = mainViewModel.darkThemeEnabled.collectAsState(initial = true).value
            EasyPromoTheme(darkTheme = darkThemePref) {
                // Directly call AppNavHost, removing all auth-related parameters
                AppNavHost(
                    isDarkTheme = darkThemePref,
                    onThemeChange = { mainViewModel.setDarkThemeEnabled(it) },
                    productViewModel = productViewModel
                )
            }
        }
    }
}