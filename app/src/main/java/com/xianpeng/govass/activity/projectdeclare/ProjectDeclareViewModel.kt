package com.xianpeng.govass.activity.projectdeclare

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.*
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import com.xianpeng.govass.fragment.policy.PolicyBase
import com.xianpeng.govass.fragment.policy.SearchPolicyBase
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus
import java.io.File

class ProjectDeclareViewModel : BaseViewModel() {
    fun saveProjectDeclare(newProjectDeclareReqVo :NewProjectDeclareReqVo, attachmentList: List<String>) {
        if (attachmentList.isNotEmpty()) {
            uploadAttachmentFileAndSave(newProjectDeclareReqVo, attachmentList)
        } else {
            save(newProjectDeclareReqVo, listOf())
        }
    }

    private fun uploadAttachmentFileAndSave(newProjectDeclareReqVo :NewProjectDeclareReqVo, attachmentList: List<String>) {
        val fileList: MutableList<File> = ArrayList()
        for (i in attachmentList.indices) {
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
                        toastError("文件上传失败，请稍后重试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    val attachmentList: MutableList<Attachment> = java.util.ArrayList()
                    for (i in response.data.indices) {
                        val attachmentItem: Attachment = response.data.get(i)
                        attachmentItem.name = attachmentItem.fileName
                        attachmentItem.url = attachmentItem.filePath
                        attachmentList.add(attachmentItem)
                    }
                    save(newProjectDeclareReqVo, attachmentList)
                }

                override fun onError(anError: ANError?) {
                    toastError("文件上传失败,failmsg=" + anError!!.errorDetail)
                }
            })
    }

    private fun save(newProjectDeclareReqVo :NewProjectDeclareReqVo, attachmentList: List<Attachment>) {
        newProjectDeclareReqVo.attachmentList = attachmentList
        AndroidNetworking.post(Constants.POST_PROJECT_DECLARE_SAVE)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addApplicationJsonBody(newProjectDeclareReqVo)
            .build()
            .getAsObject(BaseGovassResponse::class.java, object :
                ParsedRequestListener<BaseGovassResponse> {
                override fun onResponse(response: BaseGovassResponse?) {
                    if (response == null) {
                        toastError("项目申报提交失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    toastSuccess("项目申报提交成功，待管理员审核")
                    val readMsg = Msg()
                    readMsg.msg = MSGTYPE.POST_PROJECT_DECLARE_SUCCESS.name
                    EventBus.getDefault().post(readMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError("项目申报提交失败，请稍后再试")
                }
            })
    }

    fun getPolicy(keyWord:String?){
        AndroidNetworking.get(Constants.GET_POLICY_BY_SEARCH + keyWord)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(SearchPolicyBase::class.java, object :
                ParsedRequestListener<SearchPolicyBase> {
                override fun onResponse(response: SearchPolicyBase?) {
                    if (response == null) {
                        toastError("获取政策文件库数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }

                    val readMsg = Msg()
                    readMsg.msg = MSGTYPE.GET_POLICY_BY_SEARCH_SUCCESS.name
                    readMsg.searchPolicyDta = response.data!!
                    EventBus.getDefault().post(readMsg)
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }
}