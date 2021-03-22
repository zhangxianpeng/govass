package com.xianpeng.govass.fragment.dyment

import com.xianpeng.govass.bean.Attachment

class SaveDymentVo {
    var content: String = ""
    var contentType: Int = -1
    var id: Int = 0
    var title: String = ""
    var attachmentList: List<Attachment> = ArrayList()
}