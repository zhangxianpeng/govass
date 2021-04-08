package com.xianpeng.govass.fragment.mailist

import com.xianpeng.govass.bean.Attachment

/**
 * 普通消息请求VO
 */
class NewPlainMsgReqVo {
    var attachmentList: List<Attachment>? = null
    var content: String? = null
    var groupIdList: List<Int>? = null
    var id = -1
    var receiverType = -1
    var title: String? = null
    var userIdList: List<Int>? = null
    var userType = -1
}