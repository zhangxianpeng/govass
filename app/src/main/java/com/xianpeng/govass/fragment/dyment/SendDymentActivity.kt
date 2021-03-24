package com.xianpeng.govass.fragment.dyment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerPreviewActivity
import cn.bingoogolapple.photopicker.widget.BGASortableNinePhotoLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tbruyelle.rxpermissions3.RxPermissions
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants.Companion.POST_SEND_DYMENT
import com.xianpeng.govass.Constants.Companion.UPLOAD_MULTY_FILE
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
import com.xianpeng.govass.bean.BaseFileUploadRes
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastNormal
import com.xianpeng.govass.ext.toastSuccess
import com.xuexiang.xui.widget.actionbar.TitleBar
import kotlinx.android.synthetic.main.activity_send_dyment.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.io.File
import java.util.*

class SendDymentActivity : BaseActivity<BaseViewModel>(), BGASortableNinePhotoLayout.Delegate {
    var contentType: Int = -1
    var attachment: List<String>? = ArrayList()
    override fun layoutId(): Int = R.layout.activity_send_dyment

    override fun initView(savedInstanceState: Bundle?) {
        contentType= intent.getIntExtra("contentType",-1)
        titlebar.setLeftClickListener { finish() }
        titlebar.setTitle("发布动态")
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                val content: String = declareInfoEdit.text.toString().trim { it <= ' ' }
                if (content.isEmpty()) {
                    toastNormal("必须输入内容！")
                    return
                }
                var file: MutableList<File> = ArrayList()
                if (attachment!!.isNotEmpty()) {
                    showLoading("图片上传中...")
                    for (i in 0 until attachment!!.size) {
                        var fileData = File(attachment!!.get(i).trim())
                        file.add(fileData)
                    }
                    uploadFile(file, content, contentType)
                } else {
                    sendDyment(null, content, contentType)
                }
            }

            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_send_24
        })
        declareInfoPhotoLayout.setDelegate(this)
    }

    private fun uploadFile(file: MutableList<File>, content: String, contentType: Int) {
        AndroidNetworking.upload(UPLOAD_MULTY_FILE)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addMultipartFileList("file", file)
            .build()
            .getAsObject(BaseFileUploadRes::class.java, object :
                ParsedRequestListener<BaseFileUploadRes> {
                override fun onResponse(response: BaseFileUploadRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("")
                        return
                    }
                    if (response.code != 0) {
                        showMessage(response.msg)
                        return
                    }
                    var attachmtnList: MutableList<Attachment> = ArrayList()
                    for (i in 0 until response.data.size) {
                        var attachmentItem: Attachment = response.data.get(i)
                        attachmentItem.name = attachmentItem.fileName
                        attachmentItem.url = attachmentItem.filePath
                        attachmtnList.add(attachmentItem)
                    }
                    sendDyment(attachmtnList, content, contentType)
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(",failmsg=" + anError!!.errorDetail)
                }
            })
    }

    private fun sendDyment(attachmtnList: List<Attachment>?, content: String, contentType: Int) {
        Log.e("zhangxianpeng","sendDyment");
        var sendDymentReqVo = SaveDymentVo()
        sendDymentReqVo.content = content
        sendDymentReqVo.contentType = contentType  //千企 1 商业 0
        if (attachmtnList != null) {
            sendDymentReqVo.attachmentList = attachmtnList
        }
        AndroidNetworking.post(POST_SEND_DYMENT)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(sendDymentReqVo)
            .build()
            .getAsObject(BaseGovassResponse::class.java, object :
                ParsedRequestListener<BaseGovassResponse> {
                override fun onResponse(response: BaseGovassResponse?) {
                    if (response == null) {
                        toastError("动态发布失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        showMessage(response.msg)
                        return
                    }
                    toastSuccess("动态发布成功，待管理员审核")
                    finish()
                }

                override fun onError(anError: ANError?) {
                    toastError("动态发布失败，请稍后再试")
                }
            })
    }


    override fun onClickNinePhotoItem(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: ArrayList<String>?
    ) {
        val photoPickerPreviewIntent = BGAPhotoPickerPreviewActivity.IntentBuilder(this)
            .previewPhotos(models) // 当前预览的图片路径集合
            .selectedPhotos(models) // 当前已选中的图片路径集合
            .maxChooseCount(declareInfoPhotoLayout.maxItemCount) // 图片选择张数的最大值
            .currentPosition(position) // 当前预览图片的索引
            .isFromTakePhoto(false) // 是否是拍完照后跳转过来
            .build()
        startActivityForResult(photoPickerPreviewIntent, RC_PHOTO_PREVIEW)
    }

    override fun onClickAddNinePhotoItem(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        view: View?,
        position: Int,
        models: ArrayList<String>?
    ) {
        choicePhotoWrapper()
    }

    override fun onNinePhotoItemExchanged(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        fromPosition: Int,
        toPosition: Int,
        models: ArrayList<String>?
    ) {
    }

    override fun onClickDeleteNinePhotoItem(
        sortableNinePhotoLayout: BGASortableNinePhotoLayout?,
        view: View?,
        position: Int,
        model: String?,
        models: ArrayList<String>?
    ) {
        declareInfoPhotoLayout?.removeItem(position)
    }

    private fun choicePhotoWrapper() {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .subscribe {
                if (it) {
                    // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                    val takePhotoDir =
                        File(Environment.getExternalStorageDirectory(), "garbagesort")
                    val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(this)
                        .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                        .maxChooseCount(declareInfoPhotoLayout.maxItemCount - declareInfoPhotoLayout.itemCount) // 图片选择张数的最大值
                        .selectedPhotos(null) // 当前已选中的图片路径集合
                        .pauseOnScroll(true) // 滚动列表时是否暂停加载图片
                        .build()
                    startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO)
                } else {
                    toastNormal("请打开相机和存储权限后重试！")
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            attachment = BGAPhotoPickerActivity.getSelectedPhotos(data)
            declareInfoPhotoLayout?.addMoreData(BGAPhotoPickerActivity.getSelectedPhotos(data))
        } else if (requestCode == RC_PHOTO_PREVIEW) {
            declareInfoPhotoLayout?.data = BGAPhotoPickerPreviewActivity.getSelectedPhotos(data)
        }
    }

    companion object {
        private const val RC_CHOOSE_PHOTO = 1
        private const val RC_PHOTO_PREVIEW = 2
        const val EXTRA_MOMENT = "EXTRA_MOMENT"
    }
}