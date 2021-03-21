package com.xianpeng.govass.fragment.dyment

import com.xianpeng.govass.bean.Attachment

class DymentItem {
    var id: Int = -1
    var enterpriseId: Int = -1
    var enterpriseName: String = ""
    var title: String = ""
    var contentType: Int = -1
    var content: String = ""
    var status: Int = -1
    var likeCount: Int = -1
    var liked: Boolean = false
    var attachmentList: List<Attachment>? = ArrayList()
}