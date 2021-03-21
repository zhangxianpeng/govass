package com.xianpeng.govass.activity.register

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import com.xianpeng.govass.util.AESUtils
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject

class RegisterViewModel : BaseViewModel() {
    fun register(
        email: String,
        enterpriseUserType: Int,
        identityCard: String,
        mobile: String,
        password: String,
        realname: String,
        username: String
    ) {
        var registerVo = JSONObject()
        registerVo.put("email", email)
        registerVo.put("enterpriseUserType", enterpriseUserType)
        registerVo.put("identityCard", identityCard)
        registerVo.put("mobile", mobile)
        registerVo.put("password", AESUtils.encrypt(password))
        registerVo.put("realname", realname)
        registerVo.put("username", username)
        AndroidNetworking.post(Constants.DEFAULT_SERVER_REGISTER).addJSONObjectBody(registerVo)
            .build()
            .getAsObject(BaseGovassResponse::class.java, object :
                ParsedRequestListener<BaseGovassResponse> {
                override fun onResponse(response: BaseGovassResponse?) {
                    if (response == null) {
                        toastError("注册失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("注册成功")
                }

                override fun onError(anError: ANError?) {
                    toastError("注册失败，请稍后再试")
                }
            })
    }
}