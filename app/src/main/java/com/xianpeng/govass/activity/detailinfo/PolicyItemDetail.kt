package com.xianpeng.govass.activity.detailinfo

import com.xianpeng.govass.bean.Attachment

class PolicyItemDetail {
    var id: Int = -1
    var status: Int = -1
    var title: String = ""
    var content: String = ""
    var attachmentList: List<Attachment>? = ArrayList()
}