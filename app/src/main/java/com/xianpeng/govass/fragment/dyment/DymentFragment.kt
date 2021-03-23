package com.xianpeng.govass.fragment.dyment

import android.Manifest
import android.content.Intent
import android.graphics.Typeface.BOLD
import android.graphics.Typeface.NORMAL
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tbruyelle.rxpermissions3.RxPermissions
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.adapter.DymentAdapter
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.fragment_dyment.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.tab_title_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class DymentFragment : BaseFragment<BaseViewModel>(), BGANinePhotoLayout.Delegate {
    private var adapter: DymentAdapter? = null
    private var currentPhotoLayout: BGANinePhotoLayout? = null
    private var data: MutableList<DymentItem> = ArrayList()
    private var currentClickPosition = -1

    var page = 1
    var contentType = 0   // 0 千企  1 商业

    override fun layoutId(): Int {
        return R.layout.fragment_dyment
    }

    override fun initView(savedInstanceState: Bundle?) {
        multiple_status_view.setOnRetryClickListener {
            showLoading()
            initPageData(page, contentType, true)
        }
        //发布历史
        historyIv.visible(CacheUtil.getUser()?.userType != 0)
        addIv.visible(CacheUtil.getUser()?.userType != 0)
        historyIv.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    DymentHistoryActivity::class.java
                )
            )
        }
        //添加动态
        addIv.setOnClickListener {
            startActivity(
                Intent(activity, SendDymentActivity::class.java).putExtra(
                    "contentType",
                    contentType
                )
            )
        }
        //千企点击
        ll_left.setOnClickListener {
            showLoading()
            leftTabTv.setTypeface(null, BOLD)
            left_indicator.visible(true)
            rightTabTv.setTypeface(null, NORMAL)
            right_indicator.visible(false)
            contentType = 0
            page = 1
            initPageData(page, contentType, true)
        }
        //商业点击
        ll_right.setOnClickListener {
            showLoading()
            leftTabTv.setTypeface(null, NORMAL)
            left_indicator.visible(false)
            rightTabTv.setTypeface(null, BOLD)
            right_indicator.visible(true)
            contentType = 1
            page = 1
            initPageData(page, contentType, true)
        }
        recycleview.layoutManager = LinearLayoutManager(context)
        adapter = DymentAdapter(recycleview, this)
        recycleview.adapter = adapter
        recycleview.addOnScrollListener(BGARVOnScrollListener(activity))

        showLoading()
        initPageData(page, contentType, true)
    }

    private fun initPageData(page: Int, contentType: Int, isClearData: Boolean) {
        AndroidNetworking.get(Constants.GET_DYMENT_LIST)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("page", page.toString())
            .addQueryParameter("contentType", contentType.toString())
            .build().getAsObject(DymentBase::class.java, object :
                ParsedRequestListener<DymentBase> {
                override fun onResponse(response: DymentBase?) {
                    if (response == null) {
                        toastError("企业动态数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    dismissLoading()
                    var result = response.data
                    if (isClearData) {
                        data.clear();
                    }

                    if (result?.list!!.isEmpty()) {
                        multiple_status_view.showEmpty()
                    } else {
                        multiple_status_view.showContent()
                        data.addAll(result?.list!!)
                        adapter?.data = data
                        if (adapter != null) adapter!!.notifyDataSetChanged()
                    }
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
        currentPhotoLayout = ninePhotoLayout
        photoPreviewWrapper()
    }

    private fun photoPreviewWrapper() {
        currentPhotoLayout?.let { currentPhotoLayout ->
            RxPermissions(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe {
                    if (it) {
                        val photoPreviewIntentBuilder =
                            BGAPhotoPreviewActivity.IntentBuilder(activity)
                        if (currentPhotoLayout.itemCount == 1) {
                            // 预览单张图片
                            photoPreviewIntentBuilder.previewPhoto(currentPhotoLayout.currentClickItem)
                        } else if (currentPhotoLayout.itemCount > 1) {
                            // 预览多张图片
                            photoPreviewIntentBuilder.previewPhotos(currentPhotoLayout.data)
                                .currentPosition(currentPhotoLayout.currentClickItemPosition) // 当前预览图片的索引
                        }
                        startActivity(photoPreviewIntentBuilder.build())
                    }
                }
        }

    }

    override fun onClickExpand(
        ninePhotoLayout: BGANinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: MutableList<String>?
    ) {
        ninePhotoLayout?.setIsExpand(true)
        ninePhotoLayout?.flushItems()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null) {
            return
        }
//        if (resultCode == Activity.RESULT_OK && requestCode == RC_ADD_MOMENT) {
//            adapter?.addFirstItem(data.getParcelableExtra(DeclareInfoActivity.EXTRA_MOMENT))
//            dymentRv?.smoothScrollToPosition(0)
//        }
//        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PROCESS) {
//            val infoList = adapter?.data ?: return
//            if (currentClickPosition >= 0 && currentClickPosition < infoList.size) {
//                infoList[currentClickPosition].done = true
//                adapter?.notifyDataSetChanged()
//            }
//        }
    }

    companion object {
        private const val RC_ADD_MOMENT = 1
        private const val REQUEST_CODE_PROCESS = 2
    }

}