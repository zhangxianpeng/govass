package com.xianpeng.govass

/**
 * 保存一些常量信息
 */
class Constants {
    companion object {
        const val BASE_IP = "http://117.141.152.186:8005"

        /**
         * 页面跳转参数
         */
        const val BANNER_PAGE = "banner_page"
        const val POLICY_PAGE = "policy_page"
         //验证码
        const val DEFAULT_CAPTCHA_SERVER = BASE_IP + "/govass/captcha.jpg"
        //登录
        const val DEFAULT_SERVER_LOGIN = BASE_IP + "/govass/sys/login"
        //修改密码
        const val POST_UPDATE_PWD = BASE_IP + "/govass/sys/user/password"
        //注册
        const val DEFAULT_SERVER_REGISTER = BASE_IP + "/govass/sys/enterpriseuser/register"

        //文件服务器
        const val FILE_SERVER = BASE_IP + "/govass/file-server/"
        //app版本更新
        const val GET_APP_UPDATE_URL = BASE_IP + "/govass/sys/app-version/list"
        //获取用户信息
        const val GET_USER_INFO = BASE_IP + "/govass/sys/user/info"
        //政策文件库列表
        const val GET_POLICY_LIST = BASE_IP + "/govass/sys/policy/list-published?page="
        //政策文件详情
        const val GET_POLICY_DETAIL = BASE_IP + "/govass/sys/policy/info/"
        //banner数据
        const val GET_BANNER_LIST = BASE_IP + "/govass/sys/rotationplot/listForApp"
        //普通消息列表
        const val GET_NORMALMSG_LIST = BASE_IP + "/govass/sys/msg/list-me?page="
        //文件上传
        const val UPLOAD_MULTY_FILE = BASE_IP + "/govass/file/upload"
        //发布千企动态
        const val POST_SEND_DYMENT = BASE_IP + "/govass/sys/enterprise-notice/save"
        //千企动态发布历史
        const val GET_DYMENT_SEND_HISTORY = BASE_IP + "/govass/sys/enterprise-notice/list-me?page="
        //企业动态历史
        const val GET_DYMENT_LIST = BASE_IP + "/govass/sys/enterprise-notice/list"
    }
}