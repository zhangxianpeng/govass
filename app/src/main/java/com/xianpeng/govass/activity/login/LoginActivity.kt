package com.xianpeng.govass.activity.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.bumptech.glide.Glide
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants.Companion.DEFAULT_CAPTCHA_SERVER
import com.xianpeng.govass.Constants.Companion.DEFAULT_SERVER_LOGIN
import com.xianpeng.govass.Constants.Companion.GET_USER_INFO
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.main.MainActivity
import com.xianpeng.govass.activity.register.RegisterActivity
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.hideSoftKeyboard
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastWarning
import com.xianpeng.govass.util.AESUtils
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.activity_login.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject
import java.util.*

class LoginActivity : BaseActivity<BaseViewModel>() {
    private val TAG = "LoginActivity"
    private var uuid: String? = ""
    override fun layoutId(): Int = R.layout.activity_login

    private fun login(username: String, password: String, verifycode: String) {
        var encryptPwd = AESUtils.encrypt(password)
        var loginReqVo = JSONObject()
        loginReqVo.put("captcha", verifycode)
        loginReqVo.put("password", encryptPwd)
        loginReqVo.put("username", username)
        loginReqVo.put("uuid", uuid)

        AndroidNetworking.post(DEFAULT_SERVER_LOGIN).addJSONObjectBody(loginReqVo)
            .build()
            .getAsObject(BaseGovassResponse::class.java, object :
                ParsedRequestListener<BaseGovassResponse> {
                override fun onResponse(response: BaseGovassResponse?) {
                    if (response == null) {
                        toastError("登录失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        showMessage(response.msg)
                        return
                    }
                    //获取用户信息
                    getUserInfo(response.data?.token, if (switchBtn.isChecked) password else "")
                    MMKV.defaultMMKV().putString("loginToken", response.data?.token)
                    toMain()
                }

                override fun onError(anError: ANError?) {
                    toastError("登录失败，请稍后再试")
                }
            })
    }

    private fun getUserInfo(token: String?, password: String) {
        AndroidNetworking.get(GET_USER_INFO).addHeaders("token", token)
            .build()
            .getAsObject(UserInfoBase::class.java, object :
                ParsedRequestListener<UserInfoBase> {
                override fun onResponse(response: UserInfoBase?) {
                    if (response == null) {
                        toastError("获取用户信息失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    //保存登录的用户信息
                    var loginUser = UserInfo()
                    loginUser = response.data!!
                    loginUser.password = password
                    appViewModel.userinfo.value = loginUser
                    CacheUtil.setUser(appViewModel.userinfo.value)
                }

                override fun onError(anError: ANError?) {
                    toastError("获取用户信息失败，请稍后再试")
                }
            })
    }

    private fun getVevifyCode() {
        uuid = UUID.randomUUID().toString()
        var verifyCodeImgUrl = "$DEFAULT_CAPTCHA_SERVER?uuid=$uuid"
        Glide.with(this).load(verifyCodeImgUrl).placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .into(verifycodeIv)
    }

    override fun initView(savedInstanceState: Bundle?) {
        //获取验证码
        getVevifyCode()
        // 清空账号信息
        clearIv.setOnClickListener {
            usernameText.setText("")
        }
        // 登录
        loginBtn.setOnClickListener {
            var username = usernameText.text.toString().trim()
            var password = pwdText.text.toString().trim()
            var verifycode = verifyCcodeText.text.toString().trim()
            if (username.isEmpty()) {
                toastWarning("请填写账号")
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                toastWarning("请填写密码")
                return@setOnClickListener
            }
            if (verifycode.isEmpty()) {
                toastWarning("请填写验证码")
                return@setOnClickListener
            }

            login(username, password, verifycode)
        }
        // 点击去注册
        loginRegister.setOnClickListener {
            hideSoftKeyboard(this)
            startActivity(Intent(this, RegisterActivity::class.java))
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
        //验证码刷新
        verifycodeIv.setOnClickListener {
            getVevifyCode()
        }
        //忘记密码
        forgetPwdTv.setOnClickListener {
            var serverPhone = "892731274"
            showMessage("需重置密码，请联系系统管理员，联系方式：$serverPhone", positiveAction = {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$serverPhone"))
                startActivity(dialIntent)
            }, negativeButtonText = "取消")
        }
    }

    private fun toMain() {
        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
        finish()
    }
}