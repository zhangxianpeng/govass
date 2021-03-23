package com.xianpeng.govass.activity.detailinfo

import android.graphics.Bitmap
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.gyf.immersionbar.ImmersionBar
import com.just.agentweb.AgentWeb
import com.just.agentweb.DefaultWebClient
import com.just.agentweb.WebChromeClient
import com.just.agentweb.WebViewClient
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants.Companion.BANNER_PAGE
import com.xianpeng.govass.Constants.Companion.GET_POLICY_DETAIL
import com.xianpeng.govass.Constants.Companion.POLICY_PAGE
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
import com.xianpeng.govass.ext.loadRichText
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.widget.WebLayout
import kotlinx.android.synthetic.main.layout_attachment.*
import kotlinx.android.synthetic.main.layout_rich_text.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.util.*

/**
 * created by zhangxianpeng
 * 详情界面
 */
class DetailInfoActivity : BaseActivity<BaseViewModel>() {
    private var webView: AgentWeb? = null
    private var attachmentAdapter: BaseQuickAdapter<Attachment, BaseViewHolder>? = null
    private val data: MutableList<Attachment> = ArrayList()
    override fun layoutId(): Int = R.layout.activity_deteal_info
    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener { finish() }
        initAttachmentAdapter()
        var pageParam = intent.getStringExtra("pageParam")
        when (pageParam) {
            BANNER_PAGE -> {
                cardview.visible(false)
                var bannerContent = intent.getStringExtra("bannerContent")
                var contentType = (bannerContent.split(",")[0]).substring(0, 1)
                var title = (bannerContent.split(",")[0]).substring(1)
                var content = bannerContent.split(",")[1]
                titlebar.setTitle(title)
                if (contentType.equals("0")) {  //富文本
                    richtest_webview.loadRichText(content)
                } else {
                    richtest_webview.visible(false)
                    showAgentWeb(content)
                }
            }
            POLICY_PAGE -> {
                var policyId = intent.getIntExtra("policyId", -1)
                getPolicyDetail(policyId)
            }
        }
    }

    private fun initAttachmentAdapter() {
        attachmentAdapter = object :
            BaseQuickAdapter<Attachment, BaseViewHolder>(R.layout.adapter_attachment_item, data) {
            override fun convert(holder: BaseViewHolder, item: Attachment) {
                holder.setText(R.id.tv_name, item.name)
            }
        }
        rv_attachment!!.layoutManager = LinearLayoutManager(App.instance)
        rv_attachment!!.adapter = attachmentAdapter
        attachmentAdapter!!.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
//            startActivity(
//                Intent(
//                    this@DetealInfoActivity,
//                    DetealInfoActivity::class.java
//                ).putExtra("policyId", data[position].id)
//            )
        })
    }

    private fun getPolicyDetail(policyId: Int) {
        AndroidNetworking.get(GET_POLICY_DETAIL + policyId)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(PolicyItemBase::class.java, object :
                ParsedRequestListener<PolicyItemBase> {
                override fun onResponse(response: PolicyItemBase?) {
                    if (response == null) {
                        toastError("获取文件详情失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    titlebar.setTitle(response.data?.title)
                    richtest_webview.loadRichText(response.data?.content!!)
                    data.addAll(response.data?.attachmentList!!)
                    if (attachmentAdapter != null) attachmentAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    private fun showAgentWeb(url: String?) {
        webView = AgentWeb.with(this)
            .setAgentWebParent(
                rootLayout,
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
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
            .go(url)
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
            titlebar.setTitle(title)
        }
    }
}