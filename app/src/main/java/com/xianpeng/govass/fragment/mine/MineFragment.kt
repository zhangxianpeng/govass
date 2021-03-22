package com.xianpeng.govass.fragment.mine

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.login.LoginActivity
import com.xianpeng.govass.activity.updatepwd.UpdatePwdActivity
import com.xianpeng.govass.activity.userinfo.UserInfoActivity
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class MineFragment : BaseFragment<BaseViewModel>() {
    override fun layoutId(): Int = R.layout.fragment_mine

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setTitle("个人中心")
        titlebar.setLeftVisible(false)
        titlebar.addAction(msgAction)
        account.setLeftString(CacheUtil.getUser()?.realname)

        account.setOnClickListener { startActivity(Intent(activity, UserInfoActivity::class.java)) }
        tuiguang.setOnClickListener { }
        kefu.setOnClickListener { }
        switchAccount.setOnClickListener {
            showMessage("确定切换账号登录吗，确定后将清除数据！", positiveAction = {
                CacheUtil.clearUserInfo()
                val switchAccount = Intent(activity, LoginActivity::class.java)
                startActivity(switchAccount)
            }, negativeButtonText = "取消")
        }
        updatePwd.setOnClickListener {
            startActivity(Intent(activity, UpdatePwdActivity::class.java))
        }
        logout.setOnClickListener {
            showMessage("确定退出当前登陆账号吗，确定后将清除数据！", positiveAction = {
                CacheUtil.clearUserInfo()
                System.exit(0)
            }, negativeButtonText = "取消")
        }
    }

    object msgAction : TitleBar.Action {
        override fun leftPadding(): Int = 0
        override fun performAction(view: View?) {
        }

        override fun rightPadding(): Int = 0
        override fun getText(): String = ""
        override fun getDrawable(): Int = R.drawable.ic_baseline_message_24

    }
}

