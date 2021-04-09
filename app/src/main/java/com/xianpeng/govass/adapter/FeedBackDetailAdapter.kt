package com.xianpeng.govass.adapter

import android.view.View
import android.webkit.WebView
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.feedback.FeedBackInfo
import com.xianpeng.govass.ext.loadRichText
import com.xianpeng.govass.fragment.dyment.DymentItem

class FeedBackDetailAdapter(
    recyclerView: RecyclerView,
    private val delegate: BGANinePhotoLayout.Delegate
) : BGARecyclerViewAdapter<FeedBackInfo.FeedBackDetail>(recyclerView, R.layout.adapter_feedback_detail_item) {

    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        super.setItemChildListener(helper, viewType)
    }

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: FeedBackInfo.FeedBackDetail?) {
        if (helper == null || model == null) {
            return
        }
        if(model.answer!=null) {
            if (model.answer!!.isEmpty()) {
                helper.setVisibility(R.id.rl_answer, View.GONE)
            } else {
                helper.setVisibility(R.id.rl_answer, View.VISIBLE)
                val answerWebView = helper.getView<WebView>(R.id.answer_content)
                answerWebView.loadRichText(model.answer!!)
                helper.setText(R.id.answer_username, "管理员")
            }
        } else {
            helper.setVisibility(R.id.rl_answer, View.GONE)
        }
        val userName = model.username
        val enterpriseName = model.enterpriseName

        helper.setText(R.id.question_username, if(enterpriseName!=null) "$userName-$enterpriseName" else userName)
        helper.setText(R.id.question_content, model.content)
        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.question_photos)
        ninePhotoLayout.setDelegate(delegate)
        var photos: MutableList<String> = ArrayList()
        for (i in model.attachmentList!!.indices) {
            var photo = FILE_SERVER + model.attachmentList!!.get(i).url
            photos.add(photo)
        }
        ninePhotoLayout.data = photos as java.util.ArrayList<String>?
    }
}