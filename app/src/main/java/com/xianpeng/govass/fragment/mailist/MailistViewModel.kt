package com.xianpeng.govass.fragment.mailist

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.*
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus
import java.io.File

class MailistViewModel : BaseViewModel() {
    /**
     * 获取全部政府用户
     */
    fun getAllGovementUser() {
        AndroidNetworking.get(Constants.GET_ALL_GOVERMENT_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    val childModels:ArrayList<ChildRes.UserInfo> = ArrayList()
                    childModels.addAll(response.data!!)
                    val getChildDataSuccessMsg = Msg()
                    getChildDataSuccessMsg.msg = MSGTYPE.GET_ALL_GOVERMENT_USER_SUCCESS.name
                    getChildDataSuccessMsg.childDta = childModels
                    EventBus.getDefault().post(getChildDataSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 获取全部企业用户
     */
    fun getAllEnterpriseUser() {
        AndroidNetworking.get(Constants.GET_ALL_ENTERPRISE_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    val childModels:ArrayList<ChildRes.UserInfo> = ArrayList()
                    childModels.addAll(response.data!!)
                    val getChildDataSuccessMsg = Msg()
                    getChildDataSuccessMsg.msg = MSGTYPE.GET_ALL_GOVERMENT_USER_SUCCESS.name
                    getChildDataSuccessMsg.childDta = childModels
                    EventBus.getDefault().post(getChildDataSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
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
                    val addGroupSuccessMsg = Msg()
                    addGroupSuccessMsg.msg = MSGTYPE.REFRESH_GROUP_DATA.name
                    EventBus.getDefault().post(addGroupSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 删除分组
     */
    fun deleteGroup(grouId:Int) {
        var idList:MutableList<Int> = ArrayList()
        idList.add(grouId)
        AndroidNetworking.post(Constants.POST_DELETE_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(idList)
            .build().getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("删除分组失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("删除分组成功")
                    val addGroupSuccessMsg = Msg()
                    addGroupSuccessMsg.msg = MSGTYPE.REFRESH_GROUP_DATA.name
                    EventBus.getDefault().post(addGroupSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 修改分组
     */
    fun updateGroup(updateGroup: AddGroupReqVo) {
        AndroidNetworking.post(Constants.POST_UPDATE_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(updateGroup)
            .build().getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("修改分组失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("修改分组成功")
                    val addGroupSuccessMsg = Msg()
                    addGroupSuccessMsg.msg = MSGTYPE.REFRESH_GROUP_DATA.name
                    EventBus.getDefault().post(addGroupSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 查询所有分组 （是否区分政府、企业）
     */
    fun selectAllGroup(isGetGoverMentUser: Boolean) {
        AndroidNetworking.get(Constants.GET_ALL_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("type", if (isGetGoverMentUser) "0" else "1")
            .build().getAsObject(GroupRes::class.java, object :
                ParsedRequestListener<GroupRes> {
                override fun onResponse(response: GroupRes?) {
                    if (response == null) {
                        toastError("获取分组列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }

                    val groupData : MutableList<GroupRes.GroupDataList.GroupData> = ArrayList()
                    val titleGroup = GroupRes.GroupDataList.GroupData()
                    titleGroup.name = "全部联系人"
                    groupData.add(titleGroup)
                    groupData.addAll(response.data?.list!!)
                    val getGroupListSuccessMsg = Msg()
                    getGroupListSuccessMsg.msg = MSGTYPE.GET_GROUP_LIST_SUCCESS.name
                    getGroupListSuccessMsg.groupData = groupData
                    EventBus.getDefault().post(getGroupListSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 添加用户到分组
     */
    fun addUserToGroup() {

    }

    /**
     * @belong 消息
     * @description  发送消息
     * @param msgReqVo
     */
    fun sendMsg(msgReqVo: NewPlainMsgReqVo) {
        AndroidNetworking.post(Constants.POST_ADD_ALL_USER_MSG)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(msgReqVo)
            .build().getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("发送消息失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("消息发送成功")
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * @belong 上传文件后发送消息，两个接口要先后同步执行
     * @description  发送消息
     * @param attachmentList 附件列表
     * @param msgReqVo 消息类型
     */
    fun uploadMultyFileAndSendMsg(attachmentList: MutableList<String>, msgReqVo: NewPlainMsgReqVo) {
       val fileList:MutableList<File> = ArrayList()
        for(i in 0 until attachmentList.size) {
            val file = File(attachmentList[i])
            fileList.add(file)
        }

        AndroidNetworking.upload(Constants.UPLOAD_MULTY_FILE)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addMultipartFileList("file", fileList)
            .build()
            .getAsObject(BaseFileUploadRes::class.java, object :
                ParsedRequestListener<BaseFileUploadRes> {
                override fun onResponse(response: BaseFileUploadRes?) {
                    if (response == null) {
                        toastError("")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    val attachmentLis: MutableList<Attachment> = ArrayList()
                    for (i in response.data.indices) {
                        val attachmentItem: Attachment = response.data[i]
                        attachmentItem.name = attachmentItem.fileName
                        attachmentItem.url = attachmentItem.filePath
                        attachmentLis.add(attachmentItem)
                    }
                    msgReqVo.attachmentList = attachmentLis
                    sendMsg(msgReqVo)
                }

                override fun onError(anError: ANError?) {
                    toastError(",failmsg=" + anError!!.errorDetail)
                }
            })
    }

    /**
     * @description  搜索用户
     * @param getGoverMentUser 区分政府、企业
     * @param keyword 搜索关键字
     */
    fun searchUser(getGoverMentUser: Boolean, keyword: String?) {
      var url = if(getGoverMentUser) Constants.GET_SEARCH_GOVERMENT_USER_BY_USERNAME else Constants.GET_SEARCH_ENTERPRISE_USER_BY_USERNAME
        AndroidNetworking.get(url+keyword)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    if (response == null) {
                        toastError("搜索用户失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }

                    val searchDatas:ArrayList<ChildRes.UserInfo> = ArrayList()
                    searchDatas.addAll(response.data!!)
                    val searchDatasSuccessMsg = Msg()
                    searchDatasSuccessMsg.msg = MSGTYPE.GET_SEARCH_USER_SUCCESS.name
                    searchDatasSuccessMsg.searchUserDta = searchDatas
                    EventBus.getDefault().post(searchDatasSuccessMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }
}