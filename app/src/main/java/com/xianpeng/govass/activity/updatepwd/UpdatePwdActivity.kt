package com.xianpeng.govass.activity.updatepwd

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastWarning
import com.xianpeng.govass.util.AESUtils
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.checkBox
import kotlinx.android.synthetic.main.activity_login.clearIv
import kotlinx.android.synthetic.main.activity_login.loginBtn
import kotlinx.android.synthetic.main.activity_login.pwdText
import kotlinx.android.synthetic.main.activity_update_pwd.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject

class UpdatePwdActivity : BaseActivity<BaseViewModel>() {
    override fun layoutId(): Int = R.layout.activity_update_pwd

    override fun initView(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).statusBarColor(R.color.blue).fitsSystemWindows(true).init()
        titlebar.setTitle("修改密码")
        titlebar.setLeftClickListener { finish() }
        clearIv.setOnClickListener {
            usernameText.setText("")
        }
        // 登录
        loginBtn.setOnClickListener {
            var oldpwd = oldpwd.text.toString().trim()
            var password = pwdText.text.toString().trim()
            var repaeatPwd = repeatpwdText.text.toString().trim()
            if (oldpwd.isEmpty()) {
                toastWarning("请填写原密码")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                toastWarning("请填写新密码")
                return@setOnClickListener
            }
            if (repaeatPwd.isEmpty()) {
                toastWarning("请确认新密码")
                return@setOnClickListener
            }

            if (!repaeatPwd.equals(password)) {
                toastWarning("请确认密码输入是否一致")
                return@setOnClickListener
            }

            var updatePwdReqVo = JSONObject()
            updatePwdReqVo.put("password", AESUtils.encrypt(password))
            updatePwdReqVo.put("newPassword", AESUtils.encrypt(repaeatPwd))

            AndroidNetworking.post(Constants.POST_UPDATE_PWD)
                .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
                .addJSONObjectBody(updatePwdReqVo)
                .build()
                .getAsObject(BaseGovassResponse::class.java, object :
                    ParsedRequestListener<BaseGovassResponse> {
                    override fun onResponse(response: BaseGovassResponse?) {
                        if (response == null) {
                            toastError("修改密码失败，请稍后再试")
                            return
                        }
                        if (response.code != 0) {
                            showMessage(response.msg)
                            return
                        }
                        showMessage("密码修改成功，请重新登录", positiveAction = {
                            CacheUtil.clearUserInfo()
                            System.exit(0)
                        })
                    }

                    override fun onError(anError: ANError?) {
                        toastError(anError!!.errorDetail)
                    }
                })
        }

        // 密码显示
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            pwdText.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            pwdText.setSelection(pwdText.text.length)
        }
        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            repeatpwdText.transformationMethod = if (isChecked) {
                HideReturnsTransformationMethod.getInstance()
            } else {
                PasswordTransformationMethod.getInstance()
            }
            repeatpwdText.setSelection(pwdText.text.length)
        }
    }

}