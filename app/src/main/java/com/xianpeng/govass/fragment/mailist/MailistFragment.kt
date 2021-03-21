package com.xianpeng.govass.fragment.mailist

import android.os.Bundle
import androidx.core.view.isVisible
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.visible
import kotlinx.android.synthetic.main.tab_title_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel


class MailistFragment : BaseFragment<BaseViewModel>() {
    override fun layoutId(): Int = R.layout.fragment_mailist

    override fun initView(savedInstanceState: Bundle?) {
        leftTabTv.setText("政府用户")
        rightTabTv.setText("企业用户")
        historyIv.visible(false)
    }

}