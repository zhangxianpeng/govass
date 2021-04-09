package com.xianpeng.govass.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import cn.bingoogolapple.baseadapter.BGARecyclerViewAdapter
import cn.bingoogolapple.baseadapter.BGAViewHolderHelper
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.fragment.dyment.DymentItem

class DymentAdapter(
    recyclerView: RecyclerView,
    private val delegate: BGANinePhotoLayout.Delegate
) : BGARecyclerViewAdapter<DymentItem>(recyclerView, R.layout.adapter_dyment_item) {

    override fun setItemChildListener(helper: BGAViewHolderHelper?, viewType: Int) {
        super.setItemChildListener(helper, viewType)
    }

    override fun fillData(helper: BGAViewHolderHelper?, position: Int, model: DymentItem?) {
        if (helper == null || model == null) {
            return
        }
        if (model.content.isEmpty()) {
            helper.setVisibility(R.id.declare_content, View.GONE)
        } else {
            helper.setVisibility(R.id.declare_content, View.VISIBLE)
            helper.setText(R.id.declare_content, model.content)
        }
        helper.setText(R.id.declare_username, model.enterpriseName)
        val ninePhotoLayout = helper.getView<BGANinePhotoLayout>(R.id.declare_photos)
        ninePhotoLayout.setDelegate(delegate)
        var photos: MutableList<String> = ArrayList()
        for (i in model.attachmentList!!.indices) {
            var photo = FILE_SERVER + model.attachmentList!!.get(i).url
            photos.add(photo)
        }
        ninePhotoLayout.data = photos as java.util.ArrayList<String>?
    }
}