package com.xianpeng.govass.activity.common

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.Constants.Companion.GET_FEEDBACK_LIST_ALL
import com.xianpeng.govass.Constants.Companion.GET_FEEDBACK_LIST_ME
import com.xianpeng.govass.Constants.Companion.GET_HANDLED_PROJECT_LIST
import com.xianpeng.govass.Constants.Companion.GET_WAIT_PENDING_PROJECT_LIST
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.detailinfo.DetailInfoActivity
import com.xianpeng.govass.activity.feedback.FeedBackDetailActivity
import com.xianpeng.govass.activity.projectdeclare.ProjectDeclareActivity
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.fragment.working.NormalMsgBase
import com.xianpeng.govass.fragment.working.NormalMsgItem
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.tabbar.EasyIndicator
import kotlinx.android.synthetic.main.activity_common_list.*
import kotlinx.android.synthetic.main.layout_indicator.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class CommonListActivity : BaseActivity<BaseViewModel>(), OnRefreshListener, OnLoadMoreListener,
    EasyIndicator.onTabClickListener {
    private var pageParam = ""
    private var page = 1

    //消息
    private var mNormalMsgData: MutableList<NormalMsgItem> = ArrayList()
    private var msgAdapter: BaseQuickAdapter<NormalMsgItem, BaseViewHolder>? = null

    //项目申报
    private var projectStatus = 0
    private val projectDeclareTabTitle = arrayOf("已审核", "待审核")
    private var mProjectData: MutableList<ProjectDeclareBase.ProjectDeclareList.ProjectDeclareItem> =
        ArrayList()
    private var projectAdapter: BaseQuickAdapter<ProjectDeclareBase.ProjectDeclareList.ProjectDeclareItem, BaseViewHolder>? =
        null

    //问卷调查
    private var questionNaireStatus = 0
    private val questionNaireTabTitle = arrayOf("已填报", "待填报")
    private var mQuestionData: MutableList<NormalMsgItem> = ArrayList()
    private var questionAdapter: BaseQuickAdapter<NormalMsgItem, BaseViewHolder>? = null

    //系统公告
    private var mSystemNoticeData: MutableList<SystemNoticeBase.SystemNoticeList.SystemNoticeItem> =
        ArrayList()
    private var systemNoticeAdapter: BaseQuickAdapter<SystemNoticeBase.SystemNoticeList.SystemNoticeItem, BaseViewHolder>? =
        null

    //反馈历史
    private var mFeedBackData: MutableList<FeedBackBase.FeedBackList.FeedBackItem> = ArrayList()
    private var feedBackAdapter: BaseQuickAdapter<FeedBackBase.FeedBackList.FeedBackItem, BaseViewHolder>? =
        null

    override fun layoutId(): Int = R.layout.activity_common_list

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener { finish() }
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setOnRefreshListener(this)

        pageParam = intent.getStringExtra("pageParam")
        when (pageParam) {
            Constants.FEED_BACK_HIS_PAGE -> { //反馈历史
                titlebar.setTitle("反馈历史")
                titlebar.addAction(object : TitleBar.Action {
                    override fun leftPadding(): Int = 0
                    override fun performAction(view: View?) {
                        // TODO: 2021/3/27 新增反馈
                    }

                    override fun rightPadding(): Int = 0
                    override fun getText(): String = "新增反馈"
                    override fun getDrawable(): Int = 0
                })
                initFeedBackListAdapter()
                getFeedBackList(page, true)
            }
            Constants.SYSTEM_NOTICE_PAGE -> {
                titlebar.setTitle("系统公告")
                initSystemNoticeListAdapter()
                getSystemNoticeList(page, true)
            }
            Constants.PROJECT_DECLARE_PAGE -> {
                titlebar.setTitle(if (CacheUtil.getUser()!!.userType == 1) "项目申报" else "项目审批")
                iv_banner.visible(true)
                iv_banner.setImageResource(if (CacheUtil.getUser()!!.userType == 1) R.drawable.project_banner_img else R.drawable.project_banner2_img)
                easy_indicator.visible(true)
                easy_indicator.setTabTitles(projectDeclareTabTitle)
                easy_indicator.setOnTabClickListener(this)
                if (CacheUtil.getUser()!!.userType == 1) {
                    titlebar.addAction(object : TitleBar.Action {
                        override fun leftPadding(): Int = 0
                        override fun performAction(view: View?) {
                            startActivity(
                                Intent(
                                    this@CommonListActivity,
                                    ProjectDeclareActivity::class.java
                                )
                            )
                        }

                        override fun rightPadding(): Int = 0
                        override fun getText(): String = ""
                        override fun getDrawable(): Int = R.drawable.ic_add_24dp
                    })
                }
                initProjectDeclareAdapter()
                getProjectDeclareList(page, true, projectStatus)
            }
            Constants.QUESTION_NAIRE_PAGE -> {
                easy_indicator.visible(true)
                easy_indicator.setTabTitles(questionNaireTabTitle)
                titlebar.setTitle("问卷调查")
                initQuestionNaireAdapter()
                getQuestionNaireList(page, true, questionNaireStatus)
            }
            Constants.NORMAL_MSG_PAGE -> {
                titlebar.setTitle("消息列表")
                initMsgAdapter()
                getNormalMsgList(page, true)
            }
        }
    }

    //-----------消息列表------------
    private fun initMsgAdapter() {
        msgAdapter = object : BaseQuickAdapter<NormalMsgItem, BaseViewHolder>(
            R.layout.adapter_newmsg_item,
            mNormalMsgData
        ) {
            override fun convert(holder: BaseViewHolder, item: NormalMsgItem) {
                holder.setText(R.id.tv_declare_result, item.title)
                holder.setText(R.id.tv_declare_time, item.createTime)
                holder.setText(R.id.tv_declare_msg, item.content)
                holder.setGone(R.id.tv_declare_status, item.readFlag == 1)
                holder.setBackgroundResource(
                    R.id.tv_declare_status,
                    if (item.readFlag == 0) R.drawable.circle_shape_fail else R.drawable.circle_shape_success
                )

            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = msgAdapter
        msgAdapter!!.setOnItemClickListener { _, _, position ->
            startActivity(
                Intent(this, DetailInfoActivity::class.java)
                    .putExtra("pageParam", Constants.NORMAL_MSG_PAGE)
                    .putExtra("msgId", mNormalMsgData[position].id)
                    .putExtra("primaryId", mNormalMsgData[position].primaryId)
                    .putExtra("readFlag", mNormalMsgData[position].readFlag)
            )
        }
    }

    private fun getNormalMsgList(page: Int, isClearData: Boolean) {
        AndroidNetworking.get(Constants.GET_NORMALMSG_LIST + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(NormalMsgBase::class.java, object :
                ParsedRequestListener<NormalMsgBase> {
                override fun onResponse(response: NormalMsgBase?) {
                    if (response == null) {
                        toastError("获取消息列表数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (isClearData) {
                        mNormalMsgData.clear()
                    }
                    mNormalMsgData.addAll(response.data?.list!!)
                    if (msgAdapter != null) msgAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    //-----------反馈历史------------
    private fun initFeedBackListAdapter() {
        feedBackAdapter =
            object : BaseQuickAdapter<FeedBackBase.FeedBackList.FeedBackItem, BaseViewHolder>(
                R.layout.adapter_feedback_item,
                mFeedBackData
            ) {
                override fun convert(
                    holder: BaseViewHolder,
                    item: FeedBackBase.FeedBackList.FeedBackItem
                ) {
                    holder.setText(R.id.tv_title, item.title)
                    holder.setText(R.id.tv_status, if (item.status == 0) "待处理" else "已受理")
                    holder.setText(R.id.tv_creat_time, item.createTime)
                }
            }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = feedBackAdapter
        feedBackAdapter!!.setOnItemClickListener { _, _, position ->
            startActivity(
                Intent(this, FeedBackDetailActivity::class.java)
                    .putExtra("feedBackId", mFeedBackData[position].id)
            )
        }
    }

    private fun getFeedBackList(page: Int, isClearData: Boolean) {
        val url =
            if (CacheUtil.getUser()!!.userType == 0) GET_FEEDBACK_LIST_ALL else GET_FEEDBACK_LIST_ME
        AndroidNetworking.get(url + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(FeedBackBase::class.java, object :
                ParsedRequestListener<FeedBackBase> {
                override fun onResponse(response: FeedBackBase?) {
                    if (response == null) {
                        toastError("获取反馈列表数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (isClearData) {
                        mFeedBackData.clear()
                    }
                    mFeedBackData.addAll(response.data?.list!!)
                    if (feedBackAdapter != null) feedBackAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    //-----------系统公告------------
    private fun initSystemNoticeListAdapter() {
        systemNoticeAdapter = object :
            BaseQuickAdapter<SystemNoticeBase.SystemNoticeList.SystemNoticeItem, BaseViewHolder>(
                R.layout.adapter_system_notice_item, mSystemNoticeData
            ) {
            override fun convert(
                holder: BaseViewHolder,
                item: SystemNoticeBase.SystemNoticeList.SystemNoticeItem
            ) {
                holder.setText(R.id.tv_title, item.title)
                holder.setText(R.id.tv_ui_flag, "系统公告")
                holder.setText(R.id.tv_time, item.createTime)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = systemNoticeAdapter
        systemNoticeAdapter!!.setOnItemClickListener { _, _, position ->
            val title =
                mSystemNoticeData[position].contentType.toString() + mSystemNoticeData[position].title + "," + mSystemNoticeData[position].content
            startActivity(
                Intent(this, DetailInfoActivity::class.java).putExtra(
                    "pageParam",
                    Constants.BANNER_PAGE
                ).putExtra("bannerContent", title)
            )
        }
    }

    private fun getSystemNoticeList(page: Int, isClearData: Boolean) {
        AndroidNetworking.get(Constants.GET_SYSTEM_NOTICE_LIST + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(SystemNoticeBase::class.java, object :
                ParsedRequestListener<SystemNoticeBase> {
                override fun onResponse(response: SystemNoticeBase?) {
                    if (response == null) {
                        toastError("获取系统公告数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (isClearData) {
                        mSystemNoticeData.clear()
                    }
                    mSystemNoticeData.addAll(response.data?.list!!)
                    if (systemNoticeAdapter != null) systemNoticeAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    //-----------项目申报------------
    private fun initProjectDeclareAdapter() {
        projectAdapter = object :
            BaseQuickAdapter<ProjectDeclareBase.ProjectDeclareList.ProjectDeclareItem, BaseViewHolder>(
                R.layout.adapter_system_notice_item,
                mProjectData
            ) {
            override fun convert(
                holder: BaseViewHolder,
                item: ProjectDeclareBase.ProjectDeclareList.ProjectDeclareItem
            ) {
                holder.setText(R.id.tv_title, item.name)
                holder.setText(R.id.tv_ui_flag, "项目审批")
                holder.setText(R.id.tv_time, item.createTime)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = projectAdapter
        projectAdapter!!.setOnItemClickListener { _, _, position ->
//            startActivity(
//                Intent(this, DetailInfoActivity::class.java)
//                    .putExtra("pageParam", Constants.NORMAL_MSG_PAGE)
//                    .putExtra("msgId", mNormalMsgData[position].id)
//                    .putExtra("primaryId", mNormalMsgData[position].primaryId)
//                    .putExtra("readFlag", mNormalMsgData[position].readFlag)
//            )
        }
    }

    private fun getProjectDeclareList(page: Int, isClearData: Boolean, projectStatus: Int) {
        val url =
            if (projectStatus == 0) GET_HANDLED_PROJECT_LIST else GET_WAIT_PENDING_PROJECT_LIST
        AndroidNetworking.get(url + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ProjectDeclareBase::class.java, object :
                ParsedRequestListener<ProjectDeclareBase> {
                override fun onResponse(response: ProjectDeclareBase?) {
                    if (response == null) {
                        toastError("获取项目列表数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (isClearData) {
                        mProjectData.clear()
                    }
                    mProjectData.addAll(response.data?.list!!)
                    if (projectAdapter != null) projectAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    //-----------问卷调查------------
    private fun initQuestionNaireAdapter() {
        questionAdapter = object : BaseQuickAdapter<NormalMsgItem, BaseViewHolder>(
            R.layout.adapter_newmsg_item,
            mQuestionData
        ) {
            override fun convert(holder: BaseViewHolder, item: NormalMsgItem) {
                holder.setText(R.id.tv_declare_result, item.title)
                holder.setText(R.id.tv_declare_time, item.createTime)
                holder.setText(R.id.tv_declare_msg, item.content)
                holder.setBackgroundResource(
                    R.id.tv_declare_status,
                    if (item.readFlag == 0) R.drawable.circle_shape_fail else R.drawable.circle_shape_success
                )
                holder.setGone(R.id.tv_declare_status, item.readFlag == 1)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = questionAdapter
        questionAdapter!!.setOnItemClickListener { _, _, position ->
//            startActivity(
//                // TODO: 2021/3/29  问卷调查详情
//            )
        }
    }

    private fun getQuestionNaireList(page: Int, isClearData: Boolean, questionNaireStatus: Int) {
        AndroidNetworking.get(Constants.GET_WAIT_PENDING_QUESTION_LIST + page)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addPathParameter("status", questionNaireStatus.toString())
            .build().getAsObject(NormalMsgBase::class.java, object :
                ParsedRequestListener<NormalMsgBase> {
                override fun onResponse(response: NormalMsgBase?) {
                    if (response == null) {
                        toastError("获取问卷调查数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (isClearData) {
                        mNormalMsgData.clear()
                    }
                    mQuestionData.addAll(response.data?.list!!)
                    if (questionAdapter != null) questionAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        getNormalMsgList(1, true)
        refreshLayout.finishRefresh()
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        page += 1
        getNormalMsgList(page, false)
        refreshLayout.finishLoadMore()
    }

    override fun onTabClick(title: String?, position: Int) {
        projectStatus = position
        questionNaireStatus = position
        if (projectStatus == 1) {
            getProjectDeclareList(page, true, projectStatus)
        } else {
            getProjectDeclareList(page, true, projectStatus)
        }
        if (questionNaireStatus == 1) {
            getQuestionNaireList(page, true, questionNaireStatus)
        } else {
            getQuestionNaireList(page, true, questionNaireStatus)
        }
    }
}