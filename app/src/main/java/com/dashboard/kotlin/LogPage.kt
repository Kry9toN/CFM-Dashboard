package com.dashboard.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dashboard.kotlin.clashhelper.ClashConfig
import com.dashboard.kotlin.clashhelper.ClashStatus
import com.topjohnwu.superuser.Shell
import kotlinx.android.synthetic.main.fragment_log.*
import kotlinx.coroutines.*

@DelicateCoroutinesApi
class LogPage : Fragment() {
    private val job = Shell.Builder.create()
        .build().newJob().add("cat ${ClashConfig.logPath}")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lifecycleScope.launch(Dispatchers.IO) {
            val clashV = Shell.cmd("${ClashConfig.corePath} -v").exec().out.last()
            withContext(Dispatchers.Main){
                log_cat.text = "$clashV\n${readLog()}"
            }
            while (true){
                if (ClashStatus.isCmdRunning){
                    withContext(Dispatchers.Main){
                        log_cat.text = "$clashV\n${readLog()}"
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                    delay(200)
                } else {
                    delay(600)
                }
            }
        }
    }

    private fun readLog(): String{
        val lst = mutableListOf<String>()
        job.to(lst).exec()
        return lst.joinToString("\n")
    }
}