package com.xianpeng.govass.activity.projectdeclare

import com.xianpeng.govass.bean.Attachment

class NewProjectDeclareReqVo {
    var id = -1
    var address: String? = null
    var attachmentList: List<Attachment>? = null
    var contact: String? = null
    var linkman: String? = null
    var taxOfLastYear: String? = null //上年纳税额
    var outputOfLastYear: String? = null //上年产值
    var name: String? = null
    var policyName: String? = null
}