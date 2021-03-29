package com.xianpeng.govass.activity.feedback

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.common.FeedBackBase
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.fragment.working.NormalMsgBase
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class FeedBackDetailActivity : BaseActivity<BaseViewModel>() {
    //反馈历史
    private var mFeedBackData: MutableList<FeedBackBase.FeedBackList.FeedBackItem> = ArrayList()
    private var feedBackAdapter: BaseQuickAdapter<FeedBackBase.FeedBackList.FeedBackItem, BaseViewHolder>? = null

    override fun layoutId(): Int =R.layout.activity_feed_back_detail

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setTitle("政企会客间")
        titlebar.setLeftClickListener { finish() }
        initFeedBackInfoAdapter()
        var feedBackId = intent.getIntExtra("feedBackId",-1)
        getFeedBackInfo(feedBackId)
    }

    private fun initFeedBackInfoAdapter() {
        feedBackAdapter = object : BaseQuickAdapter<FeedBackBase.FeedBackList.FeedBackItem, BaseViewHolder>(
            R.layout.adapter_feedback_item, mFeedBackData
        ) {
            override fun convert(holder: BaseViewHolder, item: FeedBackBase.FeedBackList.FeedBackItem) {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_status, if(item.status == 0)"待处理" else "已受理")
                holder.setText(R.id.tv_creat_time, item.createTime)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = feedBackAdapter
    }

    private fun getFeedBackInfo(feedBackId:Int) {
        AndroidNetworking.get(Constants.GET_FEEDBACK_DETAIL + feedBackId)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(NormalMsgBase::class.java, object :
                ParsedRequestListener<NormalMsgBase> {
                override fun onResponse(response: NormalMsgBase?) {
                    if (response == null) {
                        toastError("获取反馈详情失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

//                    mNormalMsgData.addAll(response.data?.list!!)
//                    if (msgAdapter != null) msgAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }
}