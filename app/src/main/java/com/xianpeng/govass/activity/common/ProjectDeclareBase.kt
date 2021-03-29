package com.xianpeng.govass.activity.common

import com.xianpeng.govass.bean.Attachment

data class ProjectDeclareBase(
    var code: Int = -1,
    var msg: String = "",
    var data: ProjectDeclareList? = null
) {
    data class ProjectDeclareList(
        var list: List<ProjectDeclareItem>? = null
    ) {
        data class ProjectDeclareItem(
            var id: Int = -1,
            var policyName: String = "",
            var name: String = "",
            var outputOfLastYear: String = "",
            var taxOfLastYear: String = "",
            var enterpriseId: Int = -1,
            var enterpriseName: String = "",
            var address: String = "",
            var linkman: String = "",
            var contact: String = "",
            var status: Int = -1,
            var createTime: String = "",
            var projectAttachmentEntityList: List<Attachment>? = null
        )
    }
}