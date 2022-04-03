package com.dashboard.kotlin

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebViewClient
import kotlinx.android.synthetic.main.fragment_webview_page.*


class WebViewPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview_page, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("ViewCreated", "WebViewPageViewCreated")
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
                domStorageEnabled = true
                databaseEnabled = true
            }
            webViewClient = WebViewClient()
        }
    }

    override fun onPause() {
        super.onPause()
        webView.loadUrl("")
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if ((context?.resources?.configuration?.uiMode
                    ?.and(Configuration.UI_MODE_NIGHT_MASK)) == Configuration.UI_MODE_NIGHT_YES) {
                webView.settings.forceDark = WebSettings.FORCE_DARK_ON

            }else{
                webView.settings.forceDark = WebSettings.FORCE_DARK_OFF
            }
        }
        arguments?.getString("URL")?.let {
            webView.loadUrl(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("Destroy", "WebViewPageDestroyView")

    }
}