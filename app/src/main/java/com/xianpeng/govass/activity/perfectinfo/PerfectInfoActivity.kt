package com.xianpeng.govass.activity.perfectinfo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tbruyelle.rxpermissions3.RxPermissions
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
import com.xianpeng.govass.bean.BaseFileUploadSingleRes
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.ext.*
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.utils.KeyboardUtils
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.picker.widget.builder.TimePickerBuilder
import com.xuexiang.xui.widget.picker.widget.listener.OnTimeSelectListener
import com.xuexiang.xutil.data.DateUtils
import kotlinx.android.synthetic.main.activity_perfect_info.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject
import java.io.File
import java.util.*

class PerfectInfoActivity : BaseActivity<BaseViewModel>() {
    var licenseImg: String? = null
    override fun layoutId(): Int = R.layout.activity_perfect_info

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener {
            showMessage("是否需要切换账号进行使用？", positiveAction = {
                CacheUtil.clearUserInfo()
                finish()
            }, negativeButtonText = "取消")
        }
        titlebar.setTitle("完善企业信息")
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                doPerfectAction()
            }

            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_send_24
        })
        bussnisstimeEt.setOnClickListener {
            KeyboardUtils.hideSoftInput(it)
            showTimePicker()
        }

        fl_add_company_file.setOnClickListener { choicePhotoWrapper() }
    }

    private fun choicePhotoWrapper() {
        RxPermissions(this)
            .request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
            .subscribe {
                if (it) {
                    // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话就没有拍照功能
                    val takePhotoDir =
                        File(Environment.getExternalStorageDirectory(), "govassRebuild")
                    val photoPickerIntent = BGAPhotoPickerActivity.IntentBuilder(this)
                        .cameraFileDir(takePhotoDir) // 拍照后照片的存放目录，改成你自己拍照后要存放照片的目录。如果不传递该参数的话则不开启图库里的拍照功能
                        .maxChooseCount(1) // 图片选择张数的最大值
                        .selectedPhotos(null) // 当前已选中的图片路径集合
                        .pauseOnScroll(true) // 滚动列表时是否暂停加载图片
                        .build()
                    startActivityForResult(photoPickerIntent, RC_CHOOSE_PHOTO)
                } else {
                    toastNormal("请打开相机和存储权限后重试！")
                }
            }
    }

    private fun showTimePicker() {
        var mDatePicker = TimePickerBuilder(this,
            OnTimeSelectListener { date: Date?, v: View? ->
                bussnisstimeEt.setText(
                    DateUtils.date2String(
                        date,
                        DateUtils.yyyyMMdd.get()
                    )
                )
            }
        )
            .setTimeSelectChangeListener { date: Date? ->
                Log.i(
                    "pvTime",
                    "onTimeSelectChanged"
                )
            }
            .setTitleText("日期选择")
            .build()
        mDatePicker.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == RC_CHOOSE_PHOTO) {
            var result = BGAPhotoPickerActivity.getSelectedPhotos(data)
            if (result!!.isNotEmpty()) {
                showLoading("图片上传中...")
                var file = File(result.get(0).trim())
                uploadFile(file)
            }
        }
    }

    private fun uploadFile(file: File) {
        AndroidNetworking.upload(Constants.UPLOAD_MULTY_FILE_SINGLE)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addMultipartFile("file", file)
            .build()
            .getAsObject(BaseFileUploadSingleRes::class.java, object :
                ParsedRequestListener<BaseFileUploadSingleRes> {
                override fun onResponse(response: BaseFileUploadSingleRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("")
                        return
                    }
                    if (response.code != 0) {
                        showMessage(response.msg)
                        return
                    }

                    var attachment = response.data as Attachment
                    iv_add.visible(false)
                    glidePicToImg(attachment.filePath, iv_preview)
                    licenseImg = attachment.filePath
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(",failmsg=" + anError!!.errorDetail)
                }
            })
    }

    private fun doPerfectAction() {
        val name = userNameEt.checkBlank("企业名称不能为空") ?: return
        val code = realName.checkBlank("统一社会信用代码不能为空") ?: return
        val faren = phoneEt.checkBlank("法定代表人不能为空") ?: return
        val type = emailEt.checkBlank("公司类型不能为空") ?: return
        val term = idcardEt.checkBlank("经营范围不能为空") ?: return
        val time = pwdEt.checkBlank("经营期限不能为空") ?: return
        val money = repeatpwdEt.checkBlank("注册资金不能为空") ?: return
        val address = repeatpwdEt.checkBlank("公司地址不能为空") ?: return
        val setupdate = repeatpwdEt.checkBlank("成立日期不能为空") ?: return
        val licenseImg = repeatpwdEt.checkBlank("营业执照不能为空") ?: return
        var reqVo = JSONObject()
        reqVo.put("enterpriseName", name)
        reqVo.put("address", address)
        reqVo.put("businessScope", term)
        reqVo.put("businessTerm", time)
        reqVo.put("businessType", type)
        reqVo.put("enterpriseCode", code)
        reqVo.put("legalRepresentative", faren)
        reqVo.put("registeredCapital", money)
        reqVo.put("setUpDate", setupdate)
        reqVo.put("enterpriseId", CacheUtil.getUser()?.enterpriseId)
        reqVo.put("businessLicenseImg", licenseImg)
        AndroidNetworking.post(Constants.POST_ENTERPRISE_INFO).addJSONObjectBody(reqVo)
            .build()
            .getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("信息完善失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("信息完善成功，待管理员审核通过后方可正常使用")
                }

                override fun onError(anError: ANError?) {
                    toastError("信息完善失败，请稍后再试" + anError!!.errorDetail)
                }
            })
    }

    companion object {
        private const val RC_CHOOSE_PHOTO = 1
        private const val RC_PHOTO_PREVIEW = 2
        const val EXTRA_MOMENT = "EXTRA_MOMENT"
    }
}