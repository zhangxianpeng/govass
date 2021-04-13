package com.xianpeng.govass.bean

import com.xianpeng.govass.fragment.mailist.ChildRes
import com.xianpeng.govass.fragment.mailist.GroupRes

class Msg {
    var msg:String? = null
    var groupData:MutableList<GroupRes.GroupDataList.GroupData>? = null
    var childDta:ArrayList<ChildRes.UserInfo>?=null
    var searchUserDta:ArrayList<ChildRes.UserInfo>?=null
}

enum class MSGTYPE {
    REFRESH_GROUP_DATA,
    GET_GROUP_LIST_SUCCESS,
    GET_ALL_GOVERMENT_USER_SUCCESS,
    GET_SEARCH_USER_SUCCESS
}