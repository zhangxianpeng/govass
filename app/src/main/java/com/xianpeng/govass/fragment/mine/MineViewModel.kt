package com.xianpeng.govass.fragment.mine

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.activity.common.FeedBackBase
import com.xianpeng.govass.bean.BaseApiResponse
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.bean.MSGTYPE
import com.xianpeng.govass.bean.Msg
import com.xianpeng.govass.ext.toastError
import kotlinx.android.synthetic.main.fragment_dyment.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus

class MineViewModel : BaseViewModel() {
    fun checkAppNewVersion() {
        AndroidNetworking.get(Constants.GET_APP_UPDATE_URL)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(BaseApiResponse::class.java, object :
                ParsedRequestListener<BaseApiResponse> {
                override fun onResponse(response: BaseApiResponse?) {
                    if (response == null) {
                        toastError("获取新版本失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }
                    var newVersionMsg = Msg()
                    newVersionMsg.msg = MSGTYPE.GET_NEW_VERSION_SUCCESS.name
                    newVersionMsg.newVersionDta = response.data as NewVersionBean
                    EventBus.getDefault().post(newVersionMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }
}