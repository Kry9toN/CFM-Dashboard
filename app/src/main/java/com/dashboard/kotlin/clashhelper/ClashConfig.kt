package com.dashboard.kotlin.clashhelper

import android.util.Log
import com.dashboard.kotlin.GExternalCacheDir
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.*
import org.json.JSONObject
import java.io.File
import java.net.HttpURLConnection
import java.net.URL


@DelicateCoroutinesApi
object ClashConfig {

    var paths: List<String>

    init {
        System.loadLibrary("yaml-reader")
        setTemplate()
        paths = Shell.cmd(
            "mkdir -p $dataPath/run",
            "cp -f $dataPath/script/path.sc $dataPath/run/c.cfg",
            "echo '\necho \"\${CLASH_BIN_PATH};\${CLASH_CONFIG_DIR};\"' >> $dataPath/run/c.cfg",
            "$dataPath/run/c.cfg"
        ).exec().out.last().split(';')
        Shell.cmd("rm -f $dataPath/run/c.cfg").submit()
    }

    val dataPath
        get() = "/data/adb/clash"

    val corePath by lazy {
        runCatching {
            if (paths[0] == "") throw Error() else paths[0]
        }.getOrDefault("${dataPath}/core/clash")
    }

    val scriptsPath by lazy {
        runCatching {
            if (paths[1] == "") throw Error() else paths[1]
        }.getOrDefault( "${dataPath}/scripts")
    }

    val mergedConfigPath
        get() = "${dataPath}/run/config.yaml"

    val logPath
        get() = "${dataPath}/run/run.log"

    val pidPath
        get() = "${dataPath}/run/clash.pid"

    val configPath
        get() = "${dataPath}/config.yaml"

    val baseURL by lazy {
        "http://${getExternalController()}"
    }

    val dashBoard by  lazy {
        getFromFile("$GExternalCacheDir/_template", arrayOf("external-ui"))
    }

    val secret by lazy {
        getFromFile("$GExternalCacheDir/_template", arrayOf("secret"))
    }

    fun updateConfig(callBack: (r: String) -> Unit) {
        runCatching {
            mergeConfig("config_output.yaml")

            if (Shell
                    .cmd("diff '$GExternalCacheDir/config_output.yaml' '$mergedConfigPath' > /dev/null")
                    .exec()
                    .isSuccess
            ) {
                callBack("配置莫得变化")
                return
            } else {
                val cmd = Shell.cmd("cp -f '$GExternalCacheDir/config_output.yaml' '$mergedConfigPath'").exec()
                if (cmd.isSuccess.not()){
                    callBack("${cmd.out}")
                    return
                }
            }
        }.onFailure {
            callBack("合并失败啦")
            return
        }
        if (Shell.cmd("$corePath -d $dataPath -f $mergedConfigPath -t > /dev/null").exec().isSuccess)
            updateConfigNet(mergedConfigPath, callBack)
        else
            callBack("配置文件有误唉")
    }

    private fun updateConfigNet(configPath: String, callBack: (r: String) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val conn =
                    URL("${baseURL}/configs?force=false").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Authorization", "Bearer $secret")
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                conn.outputStream.use { os ->
                    os.write(
                        JSONObject(
                            mapOf(
                                "path" to configPath
                            )
                        ).toString().toByteArray()
                    )
                }

                conn.connect()
                Log.i("NET", "HTTP CODE : ${conn.responseCode}")
                conn.inputStream.use {
                    val data = it.bufferedReader().readText()
                    Log.i("NET", data)
                }
                withContext(Dispatchers.Main){
                    when (conn.responseCode){
                        204 ->
                            callBack("配置更新成功啦")
                        else ->
                            callBack("更新失败咯，状态码：${conn.responseCode}")
                    }
                }
            } catch (ex: Exception) {
                withContext(Dispatchers.Main){
                    callBack("IO操作出错，你是不是没给俺网络权限")
                }
                Log.w("NET", ex.toString())
            }
        }
    }

    private fun mergeConfig(outputFileName: String) {
        //copyFile(clashDataPath, "config.yaml")
        copyFile("${dataPath}/config", "_template")
        Shell.cmd(
            "sed -n -E '/^proxies:.*\$/,\$p' $configPath> $GExternalCacheDir/config.yaml"
        ).exec()
        mergeFile(
            "$GExternalCacheDir/_template",
            "$GExternalCacheDir/config.yaml",
            "$GExternalCacheDir/$outputFileName"
        )
        deleteFile(GExternalCacheDir, "config.yaml")
        Log.e("TAG", "mergeConfig: $GExternalCacheDir", )
    }

    private fun getExternalController(): String {

        val temp = getFromFile("$GExternalCacheDir/_template", arrayOf("external-controller"))

        return when {
            temp.trim() == "" -> "127.0.0.1:9090"
            temp.startsWith(":") -> "127.0.0.1$temp"
            else -> temp
        }
    }

    private fun setFileNR(dirPath: String, fileName: String, func: (file: String) -> Unit) {
        copyFile(dirPath, fileName)
        func("$GExternalCacheDir/${fileName}")
        Shell.cmd("cp '$GExternalCacheDir/${fileName}' '${dirPath}/${fileName}'").exec()
        deleteFile(GExternalCacheDir, fileName)
    }

    private fun copyFile(dirPath: String, fileName: String) {
        Shell.cmd("cp '${dirPath}/${fileName}' '$GExternalCacheDir/${fileName}'").exec()
        Shell.cmd("chmod +rw '$GExternalCacheDir/${fileName}'").exec()
        return
    }

    private fun deleteFile(dirPath: String, fileName: String) {
        runCatching {
            File(dirPath, fileName).delete()
        }
    }

    private external fun getFromFile(path: String, nodes: Array<String>): String
    private external fun modifyFile(path: String, node: String, value: String)
    private external fun mergeFile(
        mainFilePath: String,
        templatePath: String,
        outputFilePath: String
    )

    private fun setTemplate() = copyFile("${dataPath}/config", "_template")
}
