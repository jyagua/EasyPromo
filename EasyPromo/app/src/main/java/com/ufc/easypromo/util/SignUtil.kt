package com.ufc.easypromo.util

import java.nio.charset.StandardCharsets
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignUtil {
    fun getSign(params: Map<String, String>, secret: String): String {
        // 1. Sort parameters alphabetically by key.
        val sortedParams = params.toSortedMap()

        // 2. Concatenate the sorted key-value pairs into a single string.
        val baseString = buildString {
            sortedParams.forEach { (key, value) ->
                append(key).append(value)
            }
        }

        // 3. Use the appSecret to create an HMAC-SHA256 signature.
        val hmac = Mac.getInstance("HmacSHA256")
        val secretKey = SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256")
        hmac.init(secretKey)
        val signatureBytes = hmac.doFinal(baseString.toByteArray(StandardCharsets.UTF_8))

        // 4. Convert the binary signature to a hexadecimal string.
        return signatureBytes.joinToString("") { "%02X".format(it) }
    }
}