package com.xianpeng.govass.fragment.working

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.MSGTYPE
import com.xianpeng.govass.bean.Msg
import com.xianpeng.govass.ext.toastError
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus

class WorkingViewModel : BaseViewModel() {
    fun globalSearch(keyword: String?) {
        AndroidNetworking.get(Constants.GET_GLOBAL_SEARCH_RESULT + keyword)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(GlobalSearchBean::class.java, object :
                ParsedRequestListener<GlobalSearchBean> {
                override fun onResponse(response: GlobalSearchBean?) {
                    if (response == null) {
                        toastError("全局搜索失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }
                    val searchResultMsg = Msg()
                    searchResultMsg.msg = MSGTYPE.GET_GLOBAL_SEARCH_RESULT_SUCCESS.name
                    searchResultMsg.searchResultDta = response.data!!.list
                    EventBus.getDefault().post(searchResultMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }
}