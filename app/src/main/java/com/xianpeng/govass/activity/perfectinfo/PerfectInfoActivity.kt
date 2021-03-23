package com.xianpeng.govass.activity.perfectinfo

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class PerfectInfoActivity : BaseActivity<BaseViewModel>() {
    override fun layoutId(): Int= R.layout.activity_perfect_info

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener {  showMessage("是否需要切换账号进行使用？", positiveAction = {
            CacheUtil.clearUserInfo()
            finish()
        }, negativeButtonText = "取消") }
        titlebar.setTitle("完善企业信息")
    }

}