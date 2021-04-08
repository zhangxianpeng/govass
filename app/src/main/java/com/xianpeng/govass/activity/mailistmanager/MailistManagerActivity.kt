package com.xianpeng.govass.activity.mailistmanager

import android.os.Bundle
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import kotlinx.android.synthetic.main.activity_mailist_manager.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

// TODO: 2021/4/6 通讯录管理界面  adapter_membermanage_item
class MailistManagerActivity : BaseActivity<BaseViewModel>() {

    override fun layoutId(): Int = R.layout.activity_mailist_manager

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setTitle("选择发送到")
        titlebar.setLeftClickListener { finish() }
    }
}