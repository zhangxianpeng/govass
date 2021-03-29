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
        titlebar.setTitle("个人信息")
        titlebar.setLeftClickListener { finish() }

        username.setRightString(CacheUtil.getUser()?.username)
        email.setRightString(CacheUtil.getUser()?.email)
        mobile.setRightString(CacheUtil.getUser()?.mobile)

        var enterCard = CacheUtil.getUser()?.identityCard
        identityCard.visible(!(enterCard == null || enterCard!!.isEmpty()))
        identityCard.setRightString(enterCard)

        var enterName = CacheUtil.getUser()?.enterpriseEntity?.enterpriseName
        enterpriseName.visible(!(enterName == null || enterName!!.isEmpty()))
        enterpriseName.setRightString(enterName)

        var enterCode = CacheUtil.getUser()?.enterpriseEntity?.enterpriseCode
        enterpriseCode.visible(!(enterCode == null || enterCode!!.isEmpty()))
        enterpriseCode.setRightString(enterCode)

        var enterLegal = CacheUtil.getUser()?.enterpriseEntity?.legalRepresentative
        legalRepresentative.visible(!(enterLegal == null || enterLegal!!.isEmpty()))
        legalRepresentative.setRightString(enterLegal)

        var enterType = CacheUtil.getUser()?.enterpriseEntity?.businessType
        businessType.visible(!(enterType == null || enterType!!.isEmpty()))
        businessType.setRightString(enterType)

        var enterCapital = CacheUtil.getUser()?.enterpriseEntity?.registeredCapital
        registeredCapital.visible(!(enterCapital == null || enterCapital!!.isEmpty()))
        registeredCapital.setRightString(enterCapital)

        var enterScope = CacheUtil.getUser()?.enterpriseEntity?.businessScope
        businessScope.visible(!(enterScope == null || enterScope!!.isEmpty()))
        businessScope.setRightString(enterScope)

        var enterDate = CacheUtil.getUser()?.enterpriseEntity?.setUpDate
        setUpDate.visible(!(enterDate == null || enterDate!!.isEmpty()))
        setUpDate.setRightString(enterDate)

        var enterTerm = CacheUtil.getUser()?.enterpriseEntity?.businessTerm
        businessTerm.visible(!(enterTerm == null || enterTerm!!.isEmpty()))
        businessTerm.setRightString(enterTerm)

        var enterAddress = CacheUtil.getUser()?.enterpriseEntity?.address
        address.visible(!(enterAddress == null || enterAddress!!.isEmpty()))
        address.setRightString(enterAddress)
    }
}