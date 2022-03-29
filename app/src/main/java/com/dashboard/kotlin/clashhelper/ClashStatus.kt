package com.dashboard.kotlin.clashhelper

import android.util.Log
import com.topjohnwu.superuser.Shell
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.concurrent.thread

@DelicateCoroutinesApi
object ClashStatus {
    var statusThreadFlag: Boolean = true
        private set
    var statusRawText: String = "{\"up\":\"0\",\"down\":\"0\",\"RES\":\"0\",\"CPU\":\"0%\"}"
    fun runStatus(): Boolean {
        var isRunning = false
        thread(start = true) {
            isRunning = try {
                val conn =
                    URL(ClashConfig.baseURL).openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${ClashConfig.secret}")
                val matches = conn.inputStream.bufferedReader()
                    .readText().contains("{\"hello\":\"clash")
                matches
            } catch (ex: Exception) {
                false
            }
        }.join()
        return isRunning
    }

    fun getStatus() {
        statusThreadFlag = true
        val secret = ClashConfig.secret
        val baseURL = ClashConfig.baseURL
        GlobalScope.launch(Dispatchers.IO) {
            runCatching {
                val conn =
                    URL("${baseURL}/traffic").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer $secret")

                conn.inputStream.use {
                    var lastCpuTotal = 0L
                    var lastClashCpuTotal = 0L

                    while (statusThreadFlag) {
                        var cpuTotal = 0L
                        var clashCpuTotal = 0L
                        Shell.cmd("cat /proc/stat | grep \"cpu \"").exec().out.first()
                            .replace("\n","")
                            .replace("cpu ","")
                            .split(Regex(" +")).forEach{ str ->
                                runCatching {
                                    cpuTotal += str.toLong()
                                }
                            }
                        Shell.cmd("cat /proc/`cat ${ClashConfig.pidPath}`/stat").exec().out.first()
                            .split(Regex(" +"))
                            .filterIndexed { index, _ -> index in 13..16 }
                            .forEach{ str ->
                                runCatching {
                                    clashCpuTotal += str.toLong()
                                }
                            }
                        val cpuAVG = BigDecimal(
                            runCatching {
                                ((clashCpuTotal - lastClashCpuTotal) /
                                        (cpuTotal - lastCpuTotal).toDouble() *100)
                            }.getOrDefault(0) as Double
                        ).setScale(2, RoundingMode.HALF_UP)

                        lastClashCpuTotal = clashCpuTotal
                        lastCpuTotal = cpuTotal

                        val res = Shell.cmd(
                            "cat /proc/`cat ${ClashConfig.pidPath}`/status | grep VmRSS | awk '{print \$2}'"
                        ).exec().out.first()

                        statusRawText = it.bufferedReader().readLine()
                            .replace("}", ",\"RES\":\"$res\",\"CPU\":\"$cpuAVG%\"}")

                        Thread.sleep(600)
                    }
                }
            }.onFailure {
                Log.d("TRAFFIC-W", it.toString())
            }
        }
    }

    fun stopGetStatus() {
        statusThreadFlag = false
    }

    var isCmdRunning = false
        private set

    fun start(){
        if (isCmdRunning) return
        isCmdRunning = true
        Shell.cmd(
            "${ClashConfig.scriptsPath}/clash.service -s && ${ClashConfig.scriptsPath}/clash.tproxy -s"
        ).submit{
            isCmdRunning = false
        }
    }

    fun stop(){
        if (isCmdRunning) return
        isCmdRunning = true
        Shell.cmd(
            "${ClashConfig.scriptsPath}/clash.service -k",
            "${ClashConfig.scriptsPath}/clash.tproxy -k"
        ).submit{
            isCmdRunning = false
        }
    }

    fun switch(){
        if (isCmdRunning) return
        if (runStatus())
            stop()
        else
            start()
    }

    fun updateGeox(){
        if (isCmdRunning) return
        isCmdRunning = true
        Shell.cmd("${ClashConfig.scriptsPath}/clash.tool -u").submit{
            isCmdRunning = false
        }
    }
}