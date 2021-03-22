package com.xianpeng.govass.activity.perfectinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class PerfectInfoActivity : BaseActivity<BaseViewModel>() {
    override fun layoutId(): Int= R.layout.activity_perfect_info

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).statusBarColor(R.color.blue).fitsSystemWindows(true).init()
        titlebar.setLeftClickListener { finish() }
        titlebar.setTitle("完善企业信息")
    }

}