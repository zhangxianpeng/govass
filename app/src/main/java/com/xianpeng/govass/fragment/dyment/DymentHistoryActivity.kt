package com.xianpeng.govass.fragment.dyment

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import cn.bingoogolapple.photopicker.activity.BGAPhotoPreviewActivity
import cn.bingoogolapple.photopicker.imageloader.BGARVOnScrollListener
import cn.bingoogolapple.photopicker.widget.BGANinePhotoLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.gyf.immersionbar.ImmersionBar
import com.tbruyelle.rxpermissions3.RxPermissions
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.adapter.DymentAdapter
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.toastError
import kotlinx.android.synthetic.main.fragment_dyment.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class DymentHistoryActivity : BaseActivity<BaseViewModel>() , BGANinePhotoLayout.Delegate{
    private var adapter: DymentAdapter? = null
    private var currentPhotoLayout: BGANinePhotoLayout? = null
    private var data: MutableList<DymentItem> = ArrayList()
    var page = 1
    override fun layoutId(): Int = R.layout.activity_dyment_history

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).statusBarColor(R.color.blue).fitsSystemWindows(true).init()
        titlebar.setLeftClickListener { finish() }
        titlebar.setTitle("发布历史")

        dymentRv.layoutManager = LinearLayoutManager(this)
        adapter = DymentAdapter(dymentRv, this)
        dymentRv.adapter = adapter
        dymentRv.addOnScrollListener(BGARVOnScrollListener(this))

        showLoading()
        initPageData(page, true)
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
                            BGAPhotoPreviewActivity.IntentBuilder(this)
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

    private fun initPageData(page: Int, isClearData: Boolean) {
        AndroidNetworking.get(Constants.GET_DYMENT_SEND_HISTORY + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(DymentBase::class.java, object :
                ParsedRequestListener<DymentBase> {
                override fun onResponse(response: DymentBase?) {
                    if (response == null) {
                        toastError("历史动态数据失败，请稍后再试")
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
                    data.addAll(result?.list!!)
                    adapter?.data = data
                    if (adapter != null) adapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

}