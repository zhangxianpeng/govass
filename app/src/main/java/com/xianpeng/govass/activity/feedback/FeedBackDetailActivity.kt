package com.xianpeng.govass.activity.feedback

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.adapter.FeedBackDetailAdapter
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import com.xianpeng.govass.ext.visible
import kotlinx.android.synthetic.main.activity_feed_back_detail.*
import kotlinx.android.synthetic.main.activity_login.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject

class FeedBackDetailActivity : BaseActivity<BaseViewModel>(), BGANinePhotoLayout.Delegate {
    private var adapter: FeedBackDetailAdapter? = null
    private var mData: MutableList<FeedBackInfo.FeedBackDetail> = ArrayList()
    override fun layoutId(): Int = R.layout.activity_feed_back_detail

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setTitle("政企会客间")
        titlebar.setLeftClickListener { finish() }

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FeedBackDetailAdapter(recyclerView, this)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(BGARVOnScrollListener(this))

        btn_send.setOnClickListener {
            if(et_input.text!!.isNotEmpty()) {
                insertNewFeedBack(et_input.text.toString())
            }
        }
        val feedBackId = intent.getIntExtra("feedBackId", -1)
        getFeedBackInfo(feedBackId)
    }

    private fun insertNewFeedBack(content:String) {
        var newFeedBackReqVo = JSONObject()
        newFeedBackReqVo.put("content", content)
        newFeedBackReqVo.put("title", "")
        showLoading()
        AndroidNetworking.post(Constants.POST_ADD_NEW_FEEDBACK).addJSONObjectBody(newFeedBackReqVo)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", "")).build()
            .getAsObject(BaseGovassResponse::class.java, object :
                ParsedRequestListener<BaseGovassResponse> {
                override fun onResponse(response: BaseGovassResponse?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("消息反馈失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("消息反馈成功，请持续关注")
                    finish()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }
    private fun getFeedBackInfo(feedBackId: Int) {
        AndroidNetworking.get(Constants.GET_FEEDBACK_DETAIL + feedBackId)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(FeedBackInfo::class.java, object :
                ParsedRequestListener<FeedBackInfo> {
                override fun onResponse(response: FeedBackInfo?) {
                    if (response == null) {
                        toastError("获取反馈详情失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }
                    if(response.data == null) {
                        return
                    }
                    ll_bottom.visible(response.data!!.status != 1)
                    mData.clear()
                    mData.add(response.data!!)
                    adapter?.data = mData
                    if (adapter != null) adapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    override fun onClickNinePhotoItem(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
    }

    override fun onClickExpand(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
    }
}