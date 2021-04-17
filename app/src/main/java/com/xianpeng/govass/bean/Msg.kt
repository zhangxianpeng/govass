package com.xianpeng.govass.bean

import com.xianpeng.govass.fragment.mailist.ChildRes
import com.xianpeng.govass.fragment.mailist.GroupRes
import com.xianpeng.govass.fragment.mine.NewVersionBean
import com.xianpeng.govass.fragment.policy.PolicyItem
import com.xianpeng.govass.fragment.working.GlobalSearchBean

class Msg {
    var msg:String? = null
    var groupData:MutableList<GroupRes.GroupDataList.GroupData>? = null
    var childDta:ArrayList<ChildRes.UserInfo>?=null
    var searchUserDta:ArrayList<ChildRes.UserInfo>?=null
    var newVersionDta: NewVersionBean.NewVersionDetail? = null
    var searchResultDta:List<GlobalSearchBean.GlobalSearchBeanDetail.GlobalSearchDta>? = null
    var searchPolicyDta:List<PolicyItem>? = null
}

enum class MSGTYPE {
    REFRESH_GROUP_DATA,
    GET_GROUP_LIST_SUCCESS,
    GET_ALL_GOVERMENT_USER_SUCCESS,
    GET_SEARCH_USER_SUCCESS,
    POST_READ_PLAIN_MSG_SUCCESS,
    GET_NEW_VERSION_SUCCESS,
    GET_GLOBAL_SEARCH_RESULT_SUCCESS,
    GET_POLICY_BY_SEARCH_SUCCESS,
    POST_PROJECT_DECLARE_SUCCESS
}