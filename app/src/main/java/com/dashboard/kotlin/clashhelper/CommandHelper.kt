package com.dashboard.kotlin.clashhelper

import android.util.Log
import com.topjohnwu.superuser.Shell
import java.math.BigDecimal
import java.math.RoundingMode

object CommandHelper {
    fun autoUnitForSpeed(byte: String): String {
        val units = listOf("B/S", "KB/S", "MB/S", "GB/S")
        var index = 0
        return try {
            var double: Double = byte.toDouble()
            while (double >= 1024.00) {
                double /= 1024
                index -= -1
            }
            "${BigDecimal(double).setScale(2, RoundingMode.HALF_UP)}${units[index]}"

        } catch (ex: Exception) {
            Log.w("autoUnit", ex.toString())
            "error"
        }

    }

    fun autoUnitForSize(kbyte: String): String{
        val units = listOf("KB", "MB", "GB", "TB")

        var num =
            runCatching { kbyte.toDouble() }
            .onFailure { return "error" }
            .getOrDefault(0.00)

        for (unit in units){
            if (num >= 1024){
                num /= 1024
            }else{
                return "${BigDecimal(num).setScale(2, RoundingMode.HALF_UP)}$unit"
            }
        }
        return "error"
    }
}