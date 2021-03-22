package com.xianpeng.govass.activity.userinfo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.activity_user_info.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class UserInfoActivity : BaseActivity<BaseViewModel>() {
    override fun layoutId(): Int = R.layout.activity_user_info

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).statusBarColor(R.color.blue).fitsSystemWindows(true).init()
        titlebar.setTitle("个人信息")
        titlebar.setLeftClickListener { finish() }

        username.setRightString(CacheUtil.getUser()?.username)
        email.setRightString(CacheUtil.getUser()?.email)
        mobile.setRightString(CacheUtil.getUser()?.mobile)

        var enterCard = CacheUtil.getUser()?.identityCard
        identityCard.visible(if (enterCard == null || enterCard!!.isEmpty()) false else true)
        identityCard.setRightString(enterCard)

        var enterName = CacheUtil.getUser()?.enterpriseEntity?.enterpriseName
        enterpriseName.visible(if (enterName == null || enterName!!.isEmpty()) false else true)
        enterpriseName.setRightString(enterName)

        var enterCode = CacheUtil.getUser()?.enterpriseEntity?.enterpriseCode
        enterpriseCode.visible(if (enterCode == null || enterCode!!.isEmpty()) false else true)
        enterpriseCode.setRightString(enterCode)

        var enterLegal = CacheUtil.getUser()?.enterpriseEntity?.legalRepresentative
        legalRepresentative.visible(if (enterLegal == null || enterLegal!!.isEmpty()) false else true)
        legalRepresentative.setRightString(enterLegal)

        var enterType = CacheUtil.getUser()?.enterpriseEntity?.businessType
        businessType.visible(if (enterType == null || enterType!!.isEmpty()) false else true)
        businessType.setRightString(enterType)

        var enterCapital = CacheUtil.getUser()?.enterpriseEntity?.registeredCapital
        registeredCapital.visible(if (enterCapital == null || enterCapital!!.isEmpty()) false else true)
        registeredCapital.setRightString(enterCapital)

        var enterScope = CacheUtil.getUser()?.enterpriseEntity?.businessScope
        businessScope.visible(if (enterScope == null || enterScope!!.isEmpty()) false else true)
        businessScope.setRightString(enterScope)

        var enterDate = CacheUtil.getUser()?.enterpriseEntity?.setUpDate
        setUpDate.visible(if (enterDate == null || enterDate!!.isEmpty()) false else true)
        setUpDate.setRightString(enterDate)

        var enterTerm = CacheUtil.getUser()?.enterpriseEntity?.businessTerm
        businessTerm.visible(if (enterTerm == null || enterTerm!!.isEmpty()) false else true)
        businessTerm.setRightString(enterTerm)

        var enterAddress = CacheUtil.getUser()?.enterpriseEntity?.address
        address.visible(if (enterAddress == null || enterAddress!!.isEmpty()) false else true)
        address.setRightString(enterAddress)
    }
}