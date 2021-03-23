package com.xianpeng.govass

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.blankj.utilcode.util.ToastUtils
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.ext.toastError
import com.xuexiang.xui.XUI
//import com.xuexiang.xupdate.XUpdate
//import com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION
//import com.xuexiang.xupdate.utils.UpdateUtils
import me.hgj.jetpackmvvm.base.BaseApp


/**
 * Date: 2021-02-02
 * Desc:
 */
class App : BaseApp() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        MMKV.initialize(this.filesDir.absolutePath + "/mmkv")
        XUI.init(this)
        XUI.debug(true)
        AndroidNetworking.initialize(this)
        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY)
    }
}