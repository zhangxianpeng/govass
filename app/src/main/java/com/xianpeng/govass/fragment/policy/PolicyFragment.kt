package com.xianpeng.govass.fragment.policy

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.Constants.Companion.GET_POLICY_LIST
import com.xianpeng.govass.Constants.Companion.NORMAL_MSG_PAGE
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.common.CommonListActivity
import com.xianpeng.govass.activity.detailinfo.DetailInfoActivity
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.textview.badge.BadgeView
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.util.*

class PolicyFragment() : BaseFragment<BaseViewModel>(), OnRefreshListener, OnLoadMoreListener {
    var page = 1
    private val data: MutableList<PolicyItem> = ArrayList()
    private var policyAdapter: BaseQuickAdapter<PolicyItem, BaseViewHolder>? = null
    override fun layoutId(): Int = R.layout.fragment_policy

    override fun initView(savedInstanceState: Bundle?) {
        getUnReadMsgCountAndShow()
        titlebar.setTitle("政策文件库")
        titlebar.setLeftVisible(false)
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                startActivity(Intent(activity, CommonListActivity::class.java).putExtra("pageParam",
                    NORMAL_MSG_PAGE))
            }

            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_message_24
        })
        refreshLayout.setOnRefreshListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        initAdapter()
        initPageData(page, true)
    }

    private fun initAdapter() {
        policyAdapter = object :
            BaseQuickAdapter<PolicyItem, BaseViewHolder>(R.layout.adapter_policy_item, data) {
            override fun convert(holder: BaseViewHolder, item: PolicyItem) {
                holder.setText(R.id.tv_policy_title, item.title)
                holder.setText(R.id.tv_sender, "发布者:" + item.createUser)
                holder.setText(R.id.tv_send_time, item.createTime)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = policyAdapter
        policyAdapter!!.setOnItemClickListener(OnItemClickListener { adapter, view, position ->
            startActivity(
                Intent(
                    activity,
                    DetailInfoActivity::class.java
                ).putExtra("policyId", data[position].id).putExtra(
                    "pageParam",
                    Constants.POLICY_PAGE
                )
            )
        })
    }

    private fun initPageData(page: Int, isClearData: Boolean) {
        AndroidNetworking.get(GET_POLICY_LIST + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(PolicyBase::class.java, object :
                ParsedRequestListener<PolicyBase> {
                override fun onResponse(response: PolicyBase?) {
                    if (response == null) {
                        toastError("获取政策文件库数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    var result = response.data
                    if (isClearData) {
                        data.clear()
                    }
                    data.addAll(result?.list!!)
                    if (policyAdapter != null) policyAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        initPageData(1, true)
        refreshLayout.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page += 1
        initPageData(page, false)
        refreshLayout.finishLoadMore()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(!hidden){
            getUnReadMsgCountAndShow()
        }
    }

    private fun getUnReadMsgCountAndShow() {
        AndroidNetworking.get(Constants.GET_UNREADMSG_COUNT)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build()
            .getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("获取未读消息数失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    BadgeView(context).bindTarget(titlebar).badgeNumber = response.data!!.toInt()
                }

                override fun onError(anError: ANError?) {
                    toastError("获取未读消息数失败，请稍后再试，msg=" + anError!!.errorDetail)
                }
            })
    }
}

