package com.xianpeng.govass.fragment.mine

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.BuildConfig
import com.xianpeng.govass.Constants
import com.xianpeng.govass.Constants.Companion.NORMAL_MSG_PAGE
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.common.CommonActivity
import com.xianpeng.govass.activity.common.CommonListActivity
import com.xianpeng.govass.activity.login.LoginActivity
import com.xianpeng.govass.activity.updatepwd.UpdatePwdActivity
import com.xianpeng.govass.activity.userinfo.UserInfoActivity
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.bean.MSGTYPE
import com.xianpeng.govass.bean.Msg
import com.xianpeng.govass.ext.glidePicToCircleImg
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastNormal
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.textview.badge.BadgeView
import kotlinx.android.synthetic.main.fragment_mine.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MineFragment : BaseFragment<MineViewModel>() {
    override fun layoutId(): Int = R.layout.fragment_mine

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        getUnReadMsgCountAndShow()
        mViewModel.checkAppNewVersion()
        titlebar.setTitle("个人中心")
        titlebar.setLeftVisible(false)
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                startActivity(
                    Intent(activity, CommonListActivity::class.java).putExtra("pageParam", NORMAL_MSG_PAGE)
                )
            }
            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_message_24
        })

        version.setRightString(BuildConfig.VERSION_NAME + "        ")
        glidePicToCircleImg(CacheUtil.getUser()?.headUrl!!, head_circleImg)
        account.setLeftString(CacheUtil.getUser()?.realname)
        ll_user_info.setOnClickListener { startActivity(Intent(activity, UserInfoActivity::class.java)) }
        account.setOnClickListener { startActivity(Intent(activity, UserInfoActivity::class.java)) }

        project_declare.text = if (CacheUtil.getUser()!!.userType == 0) "项目申报" else "我的申报"
        my_msg.text = if (CacheUtil.getUser()!!.userType == 0) "我的发文" else "我的收文"
        ll_my_project.setOnClickListener {
            startActivity(Intent(activity, CommonListActivity::class.java).putExtra("pageParam", Constants.PROJECT_DECLARE_PAGE))
        }
        ll_my_article.setOnClickListener {
            if (CacheUtil.getUser()!!.userType == 0) {
                    startActivity(Intent(activity, CommonListActivity::class.java).putExtra("pageParam", Constants.SEND_OFFICIAL_DOCUMENT_PAGE))
                } else {
                    startActivity(Intent(activity, CommonListActivity::class.java).putExtra("pageParam", Constants.MY_OFFICIAL_DOCUMENT_PAGE))
                }
        }
        tuiguang.setOnClickListener { startActivity(Intent(requireActivity(), CommonActivity::class.java).putExtra("pageFlag", "tuiguang")) }
        kefu.setOnClickListener {
            val serverPhone = "892731274"
            showMessage("请联系系统管理员，联系方式：$serverPhone", positiveAction = {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$serverPhone"))
                startActivity(dialIntent)
            }, negativeButtonText = "取消")
        }
        switchAccount.setOnClickListener {
            showMessage("确定切换账号登录吗，确定后将清除数据！", positiveAction = {
                CacheUtil.clearUserInfo()
                val switchAccount = Intent(activity, LoginActivity::class.java)
                startActivity(switchAccount)
                activity?.finish()
            }, negativeButtonText = "取消")
        }
        updatePwd.setOnClickListener {
            startActivity(Intent(activity, UpdatePwdActivity::class.java))
        }
        logout.setOnClickListener {
            showMessage("确定退出当前登陆账号吗，确定后将清除数据！", positiveAction = {
                CacheUtil.clearUserInfo()
                activity?.finish()
                System.exit(0)
            }, negativeButtonText = "取消")
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) { getUnReadMsgCountAndShow() }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    private fun getUnReadMsgCountAndShow() {
        AndroidNetworking.get(Constants.GET_UNREADMSG_COUNT)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build()
            .getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("获取未读消息数失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    BadgeView(context).bindTarget(titlebar).badgeNumber = response.data.toInt()
                }

                override fun onError(anError: ANError?) {
                    toastError("获取未读消息数失败，请稍后再试，msg=" + anError!!.errorDetail)
                }
            })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Msg?) {
        dismissLoading()
        if (event?.msg == MSGTYPE.POST_READ_PLAIN_MSG_SUCCESS.name) {
            getUnReadMsgCountAndShow()
        } else if(event?.msg == MSGTYPE.GET_NEW_VERSION_SUCCESS.name) {
            BadgeView(context)
                .bindTarget(version.rightTextView)
                .setBadgeGravity(Gravity.END or Gravity.CENTER)
                .setBadgePadding(3f, true)
                .setBadgeTextSize(9f, true).badgeNumber = 1
            version.setOnClickListener {
                val uri = Uri.parse(event.newVersionDta!!.url)
                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        }
    }
}

