package com.dashboard.kotlin.clashhelper

import com.dashboard.kotlin.KV
import kotlinx.coroutines.DelicateCoroutinesApi

enum class WebUI {
    @DelicateCoroutinesApi
    Local {
        override var url = "${ClashConfig.baseURL}/ui/"
    },
    `Meta-Yacd` {
        override var url = "https://yacd.metacubex.one"
    },
    `Meta-Razord` {
        override var url = "https://clash.metacubex.one/"
    },
    Yacd {
        override var url = "https://yacd.haishan.me/"
    },
    Razord {
        override var url = "https://clash.razord.top/"
    },
    Other {
        override var url
            get() = KV.getString("Web_UI_Other", "")!!
            set(value) {
                KV.putString("Web_UI_Other", value)
            }
    };
   abstract var url: String
}