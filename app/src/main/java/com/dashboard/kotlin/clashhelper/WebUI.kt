package com.dashboard.kotlin.clashhelper

import com.dashboard.kotlin.KV
import kotlinx.coroutines.DelicateCoroutinesApi

enum class WebUI {
    @DelicateCoroutinesApi
    LOCAL {
        override var url = "${ClashConfig.baseURL}/ui/"
    },
    META {
        override var url = "https://metacubex.github.io/clash-dashboard/"
    },
    YACD {
        override var url = "https://yacd.haishan.me/"
    },
    RAZORD{
        override var url = "https://clash.razord.top/"
    },
    OTHER {
        override var url
            get() = KV.getString("Web_UI_Other", "")!!
            set(value) {
                KV.putString("Web_UI_Other", value)
            }
    };
   abstract var url: String
}