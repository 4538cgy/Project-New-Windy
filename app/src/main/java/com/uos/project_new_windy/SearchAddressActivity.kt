package com.uos.project_new_windy

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_search_address.*


class SearchAddressActivity : AppCompatActivity() {


    private var webView : WebView ?= null
    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_address)

        webView = findViewById(R.id.activity_search_address_webview)


        init_webView()




    }

    fun init_webView(){
        webView!!.apply{

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(true)
            }

            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            settings.setSupportMultipleWindows(true)
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            webViewClient = client

            webView!!.addJavascriptInterface(AndroidBridge(), "TestApp")
            webView!!.loadUrl("https://project-new-windy.web.app/")
        }
    }


    var client : WebViewClient = object : WebViewClient(){

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            return false
        }

    }

    inner class AndroidBridge{
        @JavascriptInterface
        fun setAddress(arg1: String?, arg2: String?, arg3: String?) {
            handler.post {

                
                //주소 전달
                val intent = Intent()
                intent.putExtra("address_arg1",arg1)
                intent.putExtra("address_arg2",arg2)
                intent.putExtra("address_arg3",arg3)
                setResult(Activity.RESULT_OK,intent)
                
                //activity_search_address_textview.setText(String.format("(%s) %s %s", arg1, arg2, arg3))

                // WebView를 초기화 하지않으면 재사용할 수 없음
                init_webView()
                finish()
            }
        }
    }

}