package com.xianpeng.govass.activity.splash

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.tbruyelle.rxpermissions3.RxPermissions
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.login.LoginActivity
import com.xianpeng.govass.activity.main.MainActivity
import com.xianpeng.govass.activity.perfectinfo.PerfectInfoActivity
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastNormal
import com.xianpeng.govass.ext.toastWarning
import com.xianpeng.govass.util.CacheUtil
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class SplashActivity : BaseActivity<BaseViewModel>() {

    private val SPLASH_DISPLAY_LENGHT = 3000
    private var handler: Handler? = null
    private var loginToken: String? = "";
    override fun layoutId(): Int = R.layout.activity_splash

    override fun initView(savedInstanceState: Bundle?) {
        getPermission()
        loginToken = MMKV.defaultMMKV().getString("loginToken", "")

        if (loginToken.isNullOrEmpty()) {
            toLogin()
        } else {
            if (CacheUtil.isNeedPerfectionInfo()) {
                toastWarning("此账号需要先完善企业信息方可继续使用")
                toPerfectInformationActivity()
            }else {
                toMain()
            }
        }
    }

    private fun getPermission() {
        RxPermissions(this)
            .request(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
            ).subscribe {
                if (!it) {
                    showMessage("需要开启所有申请的权限，否则部分功能无法使用")
                }
            }
    }

    private fun toMain() {
        handler = Handler()
        handler!!.postDelayed({
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGHT.toLong())
    }

    private fun toLogin() {
        handler = Handler()
        handler!!.postDelayed({
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGHT.toLong())
    }

    private fun toPerfectInformationActivity() {
        handler = Handler()
        handler!!.postDelayed({
            startActivity(Intent(this@SplashActivity, PerfectInfoActivity::class.java))
            finish()
        }, SPLASH_DISPLAY_LENGHT.toLong())
    }
}