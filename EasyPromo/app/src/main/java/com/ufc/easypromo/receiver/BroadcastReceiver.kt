package com.ufc.easypromo.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ufc.easypromo.data.PreferencesDataStore
import com.ufc.easypromo.models.checkPriceDropsAndNotify
import com.ufc.easypromo.models.productList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PriceDropReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Use a coroutine to call suspend functions
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = PreferencesDataStore(context)
            val enabled = prefs.priceDropEnabled.first()
            checkPriceDropsAndNotify(context, productList, enabled)
        }
    }
}

