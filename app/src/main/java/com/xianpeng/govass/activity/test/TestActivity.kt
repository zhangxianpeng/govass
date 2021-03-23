package com.xianpeng.govass.activity.test

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.xianpeng.govass.R
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.bean.GarbageData
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.widget.WebLayout
import kotlinx.android.synthetic.main.activity_test.*

/**
 * 测试相关的东西都放在这
 */
class TestActivity : AppCompatActivity() {

    private var webView: AgentWeb? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        getBtn.setOnClickListener {
            AndroidNetworking.get("https://www.wanandroid.com/article/top/json")
                .build()
                .getAsObject(GarbageData::class.java, object : ParsedRequestListener<GarbageData> {
                    override fun onResponse(response: GarbageData?) {
                        if (response == null) {
                            toastError("查询失败，请稍后再试")
                            return
                        }
                        if (response.code != 200) {
                            showMessage("不是垃圾")
                            return
                        }
                    }

                    override fun onError(anError: ANError?) {
                        toastError("查询失败，请稍后再试")
                    }
                })
        }

        postBtn.setOnClickListener {
            AndroidNetworking.post("https://www.wanandroid.com/user/login")
                .addBodyParameter("username", "zjam").addBodyParameter("password", "zjam")
                .build()
                .getAsObject(BaseResponse::class.java, object : ParsedRequestListener<BaseResponse> {
                    override fun onResponse(response: BaseResponse?) {
                        if (response == null) {
                            toastError("查询失败，请稍后再试")
                            return
                        }
                        if (response.code != 200) {
                            showMessage(response.msg)
                            return
                        }
                    }

                    override fun onError(anError: ANError?) {
                        toastError("查询失败，请稍后再试")
                    }
                })
        }

        webView = AgentWeb.with(this)
            .setAgentWebParent(
                rootView,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
            .useDefaultIndicator()
            .setWebChromeClient(mWebChromeClient)
            .setWebViewClient(mWebViewClient)
            .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .setWebLayout(WebLayout(this))
            .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK) //打开其他应用时，弹窗咨询用户是否前往其他应用
            .interceptUnkownUrl() //拦截找不到相关页面的Scheme
            .createAgentWeb()
            .ready()
            .go("https://www.jd.com/")
    }

    private val mWebViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            return super.shouldOverrideUrlLoading(view, request)
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        }
    }
    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
        }
    }
}