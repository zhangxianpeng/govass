package com.xianpeng.govass.fragment.mailist

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class MailistViewModel : BaseViewModel() {
    /**
     * 获取全部政府用户
     */
    fun getAllGovementUser() {

    }

    /**
     * 获取全部企业用户
     */
    fun getAllEnterpriseUser() {

    }

    /**
     * 添加分组
     */
    fun addGroup(newGroup: AddGroupReqVo) {
        AndroidNetworking.post(Constants.POST_ADD_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(newGroup)
            .build().getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("新增分组失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("新增分组成功")
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 删除分组
     */
    fun deleteGroup() {

    }

    /**
     * 修改分组
     */
    fun updateGroup() {

    }

    /**
     * 查询所有分组 （是否区分政府、企业）
     */
    fun selectAllGroup() {

    }

    /**
     * 添加用户到分组
     */
    fun addUserToGroup() {

    }

    /**
     * @belong 消息
     * @description  发送全员消息
     * @param allUserMsgReqVo
     */
    fun sendAllUserMsg(allUserMsgReqVo: NewPlainMsgReqVo) {
        AndroidNetworking.post(Constants.POST_ADD_ALL_USER_MSG)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(allUserMsgReqVo)
            .build().getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("发送全员消息失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("全员消息发送成功")
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * @belong 消息
     * @description  发送分组消息
     * @param groupId 分组
     */
    fun sendGroupMsg() {

    }

    /**
     * @belong 消息
     * @description  发送成员消息
     */
    fun sendMemberMsg() {

    }
}