package com.xianpeng.govass.activity.projectdeclare

import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.bean.Attachment
import com.xianpeng.govass.bean.BaseFileUploadRes
import com.xianpeng.govass.bean.BaseGovassResponse
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastSuccess
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import java.io.File

class ProjectDeclareViewModel : BaseViewModel() {
    fun saveProjectDeclare(
        policyName: String,
        projectName: String,
        projectAmmount: String,
        projectPayAmmount: String,
        projectAddress: String,
        projectContract: String,
        projectPhone: String,
        attachmentList: List<String>
    ) {
        if (attachmentList.isNotEmpty()) {
            uploadAttachmentFileAndSave(
                attachmentList,
                policyName,
                projectName,
                projectAmmount,
                projectPayAmmount,
                projectAddress,
                projectContract,
                projectPhone
            )
        } else {
            save(
                listOf(),
                policyName,
                projectName,
                projectAmmount,
                projectPayAmmount,
                projectAddress,
                projectContract,
                projectPhone
            )
        }
    }

    private fun uploadAttachmentFileAndSave(
        attachmentList: List<String>,
        policyName: String,
        projectName: String,
        projectAmmount: String,
        projectPayAmmount: String,
        projectAddress: String,
        projectContract: String,
        projectPhone: String
    ) {
        var fileList: MutableList<File> = ArrayList()
        for (i in attachmentList.indices) {
            var file = File(attachmentList[i])
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

                    var attachmtnList: MutableList<Attachment> = java.util.ArrayList()
                    for (i in 0 until response.data.size) {
                        var attachmentItem: Attachment = response.data.get(i)
                        attachmentItem.name = attachmentItem.fileName
                        attachmentItem.url = attachmentItem.filePath
                        attachmtnList.add(attachmentItem)
                    }
                    save(
                        attachmtnList,
                        policyName,
                        projectName,
                        projectAmmount,
                        projectPayAmmount,
                        projectAddress,
                        projectContract,
                        projectPhone
                    )
                }

                override fun onError(anError: ANError?) {
                    toastError("文件上传失败,failmsg=" + anError!!.errorDetail)
                }
            })
    }

    private fun save(
        attachmentList: List<Attachment>,
        policyName: String,
        projectName: String,
        projectAmmount: String,
        projectPayAmmount: String,
        projectAddress: String,
        projectContract: String,
        projectPhone: String
    ) {
        var newProjectDeclareReqVo = NewProjectDeclareReqVo()
        newProjectDeclareReqVo.attachmentList = attachmentList
        newProjectDeclareReqVo.policyName = policyName
        newProjectDeclareReqVo.name = projectName
        newProjectDeclareReqVo.outputOfLastYear = projectAmmount
        newProjectDeclareReqVo.taxOfLastYear = projectPayAmmount
        newProjectDeclareReqVo.address = projectAddress
        newProjectDeclareReqVo.linkman = projectContract
        newProjectDeclareReqVo.contact = projectPhone

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
                }

                override fun onError(anError: ANError?) {
                    toastError("项目申报提交失败，请稍后再试")
                }
            })
    }
}