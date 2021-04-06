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
        const val BANNER_PAGE = "banner_detail_page"                    //banner
        const val POLICY_PAGE = "policy_detail_page"                    //政策文件库
        const val NORMAL_MSG_PAGE = "normalmsg_detail_page"             //消息列表
        const val PROJECT_DECLARE_PAGE = "projectDeclareHistoryPage"    //项目申报
        const val QUESTION_NAIRE_PAGE = "questionNaireHistoryPage"      //问卷调查
        const val SYSTEM_NOTICE_PAGE = "systemNoticePage"               //系统公告
        const val FEED_BACK_HIS_PAGE = "feedBackHistoryPage"            //反馈历史

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
        const val GET_APP_UPDATE_URL = BASE_IP + "/govass/sys/app-version/lastest"

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
        const val UPLOAD_MULTY_FILE_SINGLE = BASE_IP + "/govass/file/upload"

        const val UPLOAD_MULTY_FILE = BASE_IP + "/govass/file/batchUpload"

        //发布千企动态
        const val POST_SEND_DYMENT = BASE_IP + "/govass/sys/enterprise-notice/save"

        //千企动态发布历史
        const val GET_DYMENT_SEND_HISTORY = BASE_IP + "/govass/sys/enterprise-notice/list-me?page="

        //系统公告
        const val GET_SYSTEM_NOTICE_LIST = BASE_IP + "/govass/sys/notice/list-published?page="

        //反馈历史（企业）
        const val GET_FEEDBACK_LIST_ME = BASE_IP + "/govass/sys/feedback/list-me?page="

        //反馈历史（管理员）
        const val GET_FEEDBACK_LIST_ALL = BASE_IP + "/govass/sys/feedback/list?page="

        //反馈详情
        const val GET_FEEDBACK_DETAIL = BASE_IP + "/govass/sys/feedback/info/"

        //待处理项目申报
        const val GET_WAIT_PENDING_PROJECT_LIST = BASE_IP + "/govass/sys/project/list-pending?page="

        //已处理项目申报
        const val GET_HANDLED_PROJECT_LIST = BASE_IP + "/govass/sys/project/list-handled?page="

        //调查问卷  待处理 status = 0  已处理 status=1
        const val GET_WAIT_PENDING_QUESTION_LIST =
            BASE_IP + "/govass/sys/questionnairerecord/listMe?page="

        //企业动态历史
        const val GET_DYMENT_LIST = BASE_IP + "/govass/sys/enterprise-notice/list"

        //未读消息数量
        const val GET_UNREADMSG_COUNT = BASE_IP + "/govass/sys/msg/un-read"

        //完善企业信息
        const val POST_ENTERPRISE_INFO = BASE_IP + "/govass/sys/enterprise/fill"

        //读未读消息
        const val POST_READ_NORMAL_MSG = BASE_IP + "/govass/sys/msg/readMsg/"

        //获取消息列表的附件
        const val GET_NORMAL_MSG_ATTACHMENT = BASE_IP + "/govass/sys/plainmsg/list-attachment/"

        //新增项目申报
        const val POST_PROJECT_DECLARE_SAVE = BASE_IP + "/govass/sys/project/save"

        //全局搜索
        const val GET_GLOBAL_SEARCH_RESULT = BASE_IP + "/govass/sys/common-search/list?page=1&limit=100&query="

        //全部政府用户
        const val GET_ALL_GOVERMENT_USER = BASE_IP + "/govass/sys/user/listAllGovernment"

        //全部企业用户
        const val GET_ALL_ENTERPRISE_USER = BASE_IP + "/govass/sys/enterpriseuser/listAllEnterprise"

        //全部分组
        const val GET_ALL_GROUP = BASE_IP + "/govass/sys/group/list"
    }
}