package com.xianpeng.govass.activity.common

import android.os.Bundle
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
import kotlinx.android.synthetic.main.layout_bigpicture_view.*
import kotlinx.android.synthetic.main.layout_pdf_view.*
import kotlinx.android.synthetic.main.layout_word_view.*
import kotlinx.android.synthetic.main.titlebar_layout.*

class CommonActivity : BaseActivity<CommonViewModel>() {

    override fun layoutId(): Int = R.layout.activity_common

    override fun initView(savedInstanceState: Bundle?) {
        var fileItem = intent.getSerializableExtra("fileItem") as Attachment
        var fileName = fileItem.name
        var filePath = FILE_SERVER + fileItem.url
        titlebar.setTitle(fileName)
        titlebar.setLeftClickListener { finish() }
        mViewModel.downLoadFileAndOpen(
            applicationContext,
            pdfView,
            wordView,
            photoView,
            fileName,
            filePath
        )
    }
}