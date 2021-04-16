package com.xianpeng.govass.activity.userinfo

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.common.CommonActivity
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.Attachment
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

        val headUrl = CacheUtil.getUser()?.headUrl
        head.visible(!(headUrl == null || headUrl.isEmpty()))
        Glide.with(this).load(FILE_SERVER + headUrl)
            .into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    head.setRightTvDrawableRight(resource)
                }
            })
        val headAttachment = Attachment()
        headAttachment.name = headUrl
        headAttachment.url = headUrl
        head.setOnClickListener { startActivity(Intent(this, CommonActivity::class.java).putExtra("fileItem", headAttachment)) }

        val lisence = CacheUtil.getUser()?.enterpriseEntity?.businessLicenseImg
        license.visible(!(lisence == null || lisence.isEmpty()))
        Glide.with(this)
            .load(FILE_SERVER + lisence)
            .into(object : SimpleTarget<Drawable?>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable?>?
                ) {
                    license.setRightTvDrawableRight(resource)
                }
            })
        val lisenceAttachment = Attachment()
        lisenceAttachment.name = headUrl
        lisenceAttachment.url = headUrl
        license.setOnClickListener { startActivity(Intent(this, CommonActivity::class.java).putExtra("fileItem", lisenceAttachment)) }

        val enterCard = CacheUtil.getUser()?.identityCard
        identityCard.visible(!(enterCard == null || enterCard.isEmpty()))
        identityCard.setRightString(enterCard)

        val enterName = CacheUtil.getUser()?.enterpriseEntity?.enterpriseName
        enterpriseName.visible(!(enterName == null || enterName.isEmpty()))
        enterpriseName.setRightString(enterName)

        val enterCode = CacheUtil.getUser()?.enterpriseEntity?.enterpriseCode
        enterpriseCode.visible(!(enterCode == null || enterCode.isEmpty()))
        enterpriseCode.setRightString(enterCode)

        val enterLegal = CacheUtil.getUser()?.enterpriseEntity?.legalRepresentative
        legalRepresentative.visible(!(enterLegal == null || enterLegal.isEmpty()))
        legalRepresentative.setRightString(enterLegal)

        val enterType = CacheUtil.getUser()?.enterpriseEntity?.businessType
        businessType.visible(!(enterType == null || enterType.isEmpty()))
        businessType.setRightString(enterType)

        val enterCapital = CacheUtil.getUser()?.enterpriseEntity?.registeredCapital
        registeredCapital.visible(!(enterCapital == null || enterCapital.isEmpty()))
        registeredCapital.setRightString(enterCapital)

        val enterScope = CacheUtil.getUser()?.enterpriseEntity?.businessScope
        businessScope.visible(!(enterScope == null || enterScope.isEmpty()))
        businessScope.setRightString(enterScope)

        val enterDate = CacheUtil.getUser()?.enterpriseEntity?.setUpDate
        setUpDate.visible(!(enterDate == null || enterDate.isEmpty()))
        setUpDate.setRightString(enterDate)

        val enterTerm = CacheUtil.getUser()?.enterpriseEntity?.businessTerm
        businessTerm.visible(!(enterTerm == null || enterTerm.isEmpty()))
        businessTerm.setRightString(enterTerm)

        val enterAddress = CacheUtil.getUser()?.enterpriseEntity?.address
        address.visible(!(enterAddress == null || enterAddress.isEmpty()))
        address.setRightString(enterAddress)
    }
}