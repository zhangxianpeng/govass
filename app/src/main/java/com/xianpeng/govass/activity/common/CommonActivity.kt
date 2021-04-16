package com.xianpeng.govass.activity.common

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import com.jwkj.libzxing.QRCodeManager
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.visible
import kotlinx.android.synthetic.main.layout_bigpicture_view.*
import kotlinx.android.synthetic.main.layout_pdf_view.*
import kotlinx.android.synthetic.main.layout_word_view.*
import kotlinx.android.synthetic.main.titlebar_layout.*

class CommonActivity : BaseActivity<CommonViewModel>() {

    override fun layoutId(): Int = R.layout.activity_common

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener { finish() }
        val pageFlag = intent.getStringExtra("pageFlag")
        if(pageFlag!=null) {
            pdfView.visible(false)
            wordView.visible(false)
            photoView.visible(true)
            when(pageFlag) {
                "tuiguang" -> {
                    titlebar.setTitle("扫码下载政企通")
                    var bitmap = QRCodeManager.getInstance().createQRCode("https://www.pgyer.com/0DXB",400,400)
                    photoView.setImageBitmap(bitmap)
                }
                "kefu" -> {
                    var serverPhone = "892731274"
                    showMessage("请联系系统管理员，联系方式：$serverPhone", positiveAction = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$serverPhone"))
                        startActivity(dialIntent)
                    }, negativeButtonText = "取消")
                }
            }
        }

        val fileItem = intent.getSerializableExtra("fileItem")
        if(fileItem!=null) {
            fileItem as Attachment
            val fileName = fileItem.name
            val filePath = FILE_SERVER + fileItem.url
            titlebar.setTitle(fileName)
            mViewModel.downLoadFileAndOpen(applicationContext, pdfView, wordView, photoView, fileName!!, filePath)
        }
    }
}