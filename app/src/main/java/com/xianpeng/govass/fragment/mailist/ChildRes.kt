package com.xianpeng.govass.fragment.mailist

class ChildRes(
    var code: Int = -1,
    var msg: String = "",
    var data: List<UserInfo>? = null
) {
    data class UserInfo(
        var userId: Int = -1,
        var password: String = "",
        var realname: String = "",
        var username: String = "",
        var headUrl: String = "",
        var userType: Int = -1,  //管理员0  企业用户1
        var email: String = "",
        var mobile: String = "",
        var identityCard: String = "",
        var enterpriseId: Int = -1,// 企业Id
        var enterpriseUserType: Int = -1,//企业用户类型
        var id: Int = -1,
        var enterpriseName: String = "", //企业名称
        var enterpriseCode: String = "", //企业代码
        var legalRepresentative: String = "",  //法人
        var businessType: String = "", //商业类型
        var businessScope: String = "", //业务范围
        var registeredCapital: String = "", //注册资金
        var setUpDate: String = "",//成立日期
        var businessTerm: String = "",//
        var address: String = "",//地址
        var businessLicenseImg: String = ""
    )
}