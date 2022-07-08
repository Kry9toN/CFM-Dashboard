package com.dashboard.kotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_ip_check_page.*
import kotlinx.android.synthetic.main.fragment_ip_check_page_ip.*
import kotlinx.android.synthetic.main.fragment_ip_cleck_page_web.*
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.URL

@DelicateCoroutinesApi
class IpCheckPage : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var coroutineScope: Job
    private lateinit var sukkAPiThreadContext: ExecutorCoroutineDispatcher
    private lateinit var ipipNetThreadContext: ExecutorCoroutineDispatcher
    private lateinit var ipSbApiThreadContext: ExecutorCoroutineDispatcher
    private lateinit var sukkaGlobalThreadContext: ExecutorCoroutineDispatcher
    private lateinit var baiduCheckThreadContext: ExecutorCoroutineDispatcher
    private lateinit var netEaseCheckThreadContext: ExecutorCoroutineDispatcher
    private lateinit var githubCheckThreadContext: ExecutorCoroutineDispatcher
    private lateinit var youtubeCheckThreadContext: ExecutorCoroutineDispatcher

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ip_check_page, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        startCheck()
        super.onViewCreated(view, savedInstanceState)
        swipeView.setOnRefreshListener(this)
        Log.d("ViewCreated", "ipCheckPageViewCreated")
    }

    @OptIn(ObsoleteCoroutinesApi::class)
    private fun startCheck() {
        swipeView.isRefreshing = true
        sukkAPiThreadContext = newSingleThreadContext("sukkAPiThread")
        ipipNetThreadContext = newSingleThreadContext("ipipNetThread")
        ipSbApiThreadContext = newSingleThreadContext("ipSbApiThread")
        sukkaGlobalThreadContext = newSingleThreadContext("sukkaGlobalThread")
        baiduCheckThreadContext = newSingleThreadContext("baiduCheckThread")
        netEaseCheckThreadContext = newSingleThreadContext("netEaseCheckThread")
        githubCheckThreadContext = newSingleThreadContext("githubCheckThread")
        youtubeCheckThreadContext = newSingleThreadContext("youtubeCheckThread")

        coroutineScope = lifecycleScope.launch(Dispatchers.IO) {
            launch(sukkAPiThreadContext) {
                val tempStr: String = runCatching {
                    val sukkaApiObj =
                        JSONObject(URL("https://forge.speedtest.cn/api/location/info").readText())
                    "${sukkaApiObj.optString("full_ip")}\n" +
                            "${sukkaApiObj.optString("country")} " +
                            "${sukkaApiObj.optString("province")} " +
                            "${sukkaApiObj.optString("city")} " +
                            "${sukkaApiObj.optString("distinct")} " +
                            sukkaApiObj.optString("isp")
                }.getOrDefault("error")

                withContext(Dispatchers.Main) {
                    runCatching {
                        sukka_api_result.text = tempStr
                    }
                }
            }


            launch(ipipNetThreadContext) {
                //IPIP.NET
                val tempStr = runCatching {
                    var ipipNetText = URL("https://myip.ipip.net").readText()
                    ipipNetText = ipipNetText.replace("当前 IP：", "")
                    ipipNetText = ipipNetText.replace("来自于：", "\n")
                    ipipNetText = ipipNetText.substring(0, ipipNetText.length - 1)
                    ipipNetText
                }.getOrDefault("error")
                withContext(Dispatchers.Main) {
                    runCatching {
                        ipip_net_result.text = tempStr
                    }
                }
            }


            //IP.SB Api
            launch(ipSbApiThreadContext) {   // IP.SB API
                val tempStr = runCatching {
                    val ipsbObj = JSONObject(URL("https://api.ip.sb/geoip").readText())
                    "${ipsbObj.optString("ip")}\n" +
                            "${ipsbObj.optString("country_code")} ${ipsbObj.optString("organization")}"
                }.getOrDefault("error")
                withContext(Dispatchers.Main) {
                    runCatching {
                        ip_sb_result.text = tempStr
                    }
                }
            }


            //Sukka Global Api
            launch(sukkaGlobalThreadContext) {
                val tempStr = runCatching {
                    var ipSkkRip = URL("https://ip.skk.moe/cdn-cgi/trace").readText()
                    ipSkkRip = ipSkkRip.replaceBefore("ip=", "")
                    ipSkkRip = ipSkkRip.replaceAfter("ts=", "")
                    ipSkkRip = ipSkkRip.replace("\nts=", "")
                    ipSkkRip = ipSkkRip.replace("ip=", "")

                    runCatching {
                        val conn = URL("https://ipapi.co/${ipSkkRip}/json/").openConnection()
                        conn.setRequestProperty(
                            "user-agent",
                            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_5) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/91.0.4472.101 Safari/537.36"
                        )
                        val ipSkkGeoIpObj = JSONObject(conn.getInputStream().reader().readText())

                        "${ipSkkRip}\n${ipSkkGeoIpObj.optString("country_code")} " +
                                "${ipSkkGeoIpObj.optString("region")} " +
                                "${ipSkkGeoIpObj.optString("city")} " +
                                "${ipSkkGeoIpObj.optString("org")} "
                    }.onFailure {
                        Log.e("TAG", "startCheck: $it", )
                    }.getOrDefault(ipSkkRip)

                }.getOrDefault("error")
                withContext(Dispatchers.Main) {
                    runCatching {
                        sukka_api_global_result.text = tempStr
                    }
                }
            }

            // 下
            launch(baiduCheckThreadContext) {
                val tempStr = runCatching {
                    val conn = URL("https://baidu.com/").openConnection()
                    conn.connectTimeout = 1000 * 10
                    conn.readTimeout = 1000 * 10
                    val start = System.currentTimeMillis()
                    if (conn.getInputStream().reader().readText() != "")
                        "${System.currentTimeMillis() - start}ms"
                    else
                        "无法访问"
                }.getOrDefault("无法访问")
                withContext(Dispatchers.Main) {
                    runCatching {
                        val color = if (tempStr == "无法访问") ResourcesCompat.getColor(
                            resources, R.color.orange, context?.theme
                        ) else ResourcesCompat.getColor(resources, R.color.green, context?.theme)
                        baiduCheck.text = tempStr
                        baiduCheck.setTextColor(color)
                    }
                }
            }

            launch(netEaseCheckThreadContext) {
                val tempStr = runCatching {
                    val conn = URL("https://music.163.com/").openConnection()
                    conn.connectTimeout = 1000 * 10
                    conn.readTimeout = 1000 * 10
                    val start = System.currentTimeMillis()
                    if (conn.getInputStream().reader().readText() != "")
                        "${System.currentTimeMillis() - start}ms"
                    else
                        "无法访问"
                }.getOrDefault("无法访问")
                withContext(Dispatchers.Main) {
                    runCatching {
                        val color = if (tempStr == "无法访问") ResourcesCompat.getColor(
                            resources, R.color.orange, context?.theme
                        ) else ResourcesCompat.getColor(resources, R.color.green, context?.theme)
                        neteaseMusicCheck.text = tempStr
                        neteaseMusicCheck.setTextColor(color)
                    }
                }

            }
            launch(githubCheckThreadContext) {
                val tempStr = runCatching {
                    val conn = URL("https://github.com/").openConnection()
                    conn.connectTimeout = 1000 * 10
                    conn.readTimeout = 1000 * 10
                    val start = System.currentTimeMillis()
                    if (conn.getInputStream().reader().readText() != "")
                        "${System.currentTimeMillis() - start}ms"
                    else
                        "无法访问"
                }.getOrDefault("无法访问")
                withContext(Dispatchers.Main) {
                    runCatching {
                        val color = if (tempStr == "无法访问") ResourcesCompat.getColor(
                            resources, R.color.orange, context?.theme
                        ) else ResourcesCompat.getColor(resources, R.color.green, context?.theme)
                        githubCheck.text = tempStr
                        githubCheck.setTextColor(color)
                    }
                }

            }
            launch(youtubeCheckThreadContext) {
                val tempStr = runCatching {
                    val conn = URL("https://www.youtube.com/").openConnection()
                    conn.connectTimeout = 1000 * 10
                    conn.readTimeout = 1000 * 10
                    val start = System.currentTimeMillis()
                    if (conn.getInputStream().reader().readText() != "")
                        "${System.currentTimeMillis() - start}ms"
                    else
                        "无法访问"
                }.getOrDefault("无法访问")
                withContext(Dispatchers.Main) {
                    runCatching {
                        val color = if (tempStr == "无法访问") ResourcesCompat.getColor(
                            resources, R.color.orange, context?.theme
                        ) else ResourcesCompat.getColor(resources, R.color.green, context?.theme)
                        youtubeCheck.text = tempStr
                        youtubeCheck.setTextColor(color)
                    }
                }

            }
        }
        lifecycleScope.launch {
            coroutineScope.join()
            swipeView.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        try {
            sukkAPiThreadContext.close()
            ipipNetThreadContext.close()
            ipSbApiThreadContext.close()
            sukkaGlobalThreadContext.close()
            baiduCheckThreadContext.close()
            netEaseCheckThreadContext.close()
            githubCheckThreadContext.close()
            youtubeCheckThreadContext.close()


            coroutineScope.cancel()

            sukka_api_result.text = ""
            ipip_net_result.text = ""
            ip_sb_result.text = ""
            sukka_api_global_result.text = ""
        } finally {
            Log.d("ViewDestroy", "ipCheckPageDestroyView")
        }
        super.onDestroyView()

    }

    override fun onRefresh() {
        sukka_api_result.text = ""
        ipip_net_result.text = ""
        ip_sb_result.text = ""
        sukka_api_global_result.text = ""
        baiduCheck.text = ""
        neteaseMusicCheck.text = ""
        githubCheck.text = ""
        youtubeCheck.text = ""
        startCheck()
    }

}