package com.xianpeng.govass.fragment.working

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
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
import com.wyt.searchbox.SearchFragment
import com.wyt.searchbox.custom.IOnSearchClickListener
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.Constants.Companion.BANNER_PAGE
import com.xianpeng.govass.Constants.Companion.FEED_BACK_HIS_PAGE
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.Constants.Companion.NORMAL_MSG_PAGE
import com.xianpeng.govass.Constants.Companion.PROJECT_DECLARE_PAGE
import com.xianpeng.govass.Constants.Companion.QUESTION_NAIRE_PAGE
import com.xianpeng.govass.Constants.Companion.SYSTEM_NOTICE_PAGE
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.common.CommonListActivity
import com.xianpeng.govass.activity.detailinfo.DetailInfoActivity
import com.xianpeng.govass.activity.login.LoginActivity
import com.xianpeng.govass.adapter.RecyclerViewBannerAdapter
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.bean.BaseResponse
import com.xianpeng.govass.bean.MSGTYPE
import com.xianpeng.govass.bean.Msg
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.fragment.mailist.ChildRes
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.banner.recycler.BannerLayout
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.textview.badge.BadgeView
import kotlinx.android.synthetic.main.fragment_working.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class WorkingFragment : BaseFragment<WorkingViewModel>(), IOnSearchClickListener, OnRefreshListener,
    OnLoadMoreListener, BannerLayout.OnBannerItemClickListener {
    private val searchFragment by lazy { SearchFragment.newInstance() }
    private var page = 1
    private var mBannerOriginalData: MutableList<com.xianpeng.govass.fragment.working.BannerItem> =
        ArrayList()

    //消息列表
    private var mNormalMsgData: MutableList<NormalMsgItem> = ArrayList()
    private var msgAdapter: BaseQuickAdapter<NormalMsgItem, BaseViewHolder>? = null
    private var mBannerData: List<BannerItem>? = null

    private var bannerAdapter: RecyclerViewBannerAdapter? = null
    var urls = arrayOf( //640*360 360/640=0.5625
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEMKAqaMvNAZl2TlD8UrDOKiFY.J6wfyK3oFZdj72OfWGZdxAJ*izVDHnN8VIR6mePwfGhnRC*8vzwAXSJ*XeAw0!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //党建进行时
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEAWtyxWv3gA*DuVXG7azyf*os6zhNqQwTYARQg2dn*IHISuQI2.UJEIDTYiVDq8Nreu4T0wRrzXPO80*Lx*7n9g!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //服务企业会客厅
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEEUE4yKjqRCqilKCBk78*KUjYTJifUgrCWYKIAdx2H2pUaXp.hEZLam0Nvy4k5h7O08yk0GQqYRJY2CjxTpD60o!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //系统公告
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEDE8UAagar2fCb3mlx9rvhmkP1eP6t.5*gjvZcI3pW*01mZAv8HA328TaSlSMnXEQDmD*EU52W9mo1ZXOuREJUo!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //问卷调查
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrENtQ9.y03w9xkMMD4DlbgYwbRBOesW.rOoM8qBNFmrwfX0xjSSHPEwuD3bh8Dt323jtH6yFCabBMDiyP.eVBHks!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4"  //项目申报
    )

    override fun layoutId(): Int = R.layout.fragment_working
    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        getUnReadMsgCountAndShow()
        titlebar.setLeftVisible(false)
        titlebar.setTitle("")
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                searchFragment.showFragment(activity?.supportFragmentManager, SearchFragment.TAG)
            }

            override fun rightPadding(): Int = 30
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_search_white_24dp
        })
        titlebar.addAction(object : TitleBar.Action {
            override fun leftPadding(): Int = 0
            override fun performAction(view: View?) {
                startActivity(
                    Intent(activity, CommonListActivity::class.java).putExtra(
                        "pageParam",
                        NORMAL_MSG_PAGE
                    )
                )
            }

            override fun rightPadding(): Int = 0
            override fun getText(): String = ""
            override fun getDrawable(): Int = R.drawable.ic_baseline_message_24
        })
        searchFragment.setOnSearchClickListener(this)
        refreshLayout.setOnLoadMoreListener(this)
        refreshLayout.setOnRefreshListener(this)
        bannerAdapter = RecyclerViewBannerAdapter(urls)
        banner2.setAdapter(bannerAdapter)
        bannerAdapter!!.setOnBannerItemClickListener(this)
        initMsgAdapter()
        initPageData()
    }

    private fun initMsgAdapter() {
        msgAdapter = object : BaseQuickAdapter<NormalMsgItem, BaseViewHolder>(R.layout.adapter_normalmsg_item, mNormalMsgData) {
            override fun convert(holder: BaseViewHolder, item: NormalMsgItem) {
                holder.setText(R.id.tv_title, item.title)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = msgAdapter
        msgAdapter!!.setOnItemClickListener { _, _, position ->
            startActivity(
                Intent(activity, DetailInfoActivity::class.java)
                    .putExtra("pageParam", NORMAL_MSG_PAGE)
                    .putExtra("msgId", mNormalMsgData.get(position).id)
                    .putExtra("primaryId", mNormalMsgData.get(position).primaryId)
            )
        }
    }

    private fun initPageData() {
        getBannerData()
        getNormalMsgList(page, true)
    }

    private fun getBannerData() {
        AndroidNetworking.get(Constants.GET_BANNER_LIST)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(BannerBase::class.java, object :
                ParsedRequestListener<BannerBase> {
                override fun onResponse(response: BannerBase?) {
                    if (response == null) {
                        toastError("获取banner数据失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        if (response.msg == "token失效，请重新登录") {
                            CacheUtil.clearUserInfo()
                            activity!!.finish()
                            startActivity(Intent(activity, LoginActivity::class.java))
                        }
                    }
                    mBannerOriginalData.addAll(response.data)
                    mBannerData = transData(mBannerOriginalData)
                    ad_banner.setSource(mBannerData)
                        .setOnItemClickListener { view: View?, t: BannerItem?, position: Int ->
                            startActivity(
                                Intent(activity, DetailInfoActivity::class.java).putExtra(
                                    "pageParam",
                                    BANNER_PAGE
                                ).putExtra("bannerContent", t?.title!!)
                            )
                        }.setIsOnePageLoop(false).startScroll()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    private fun transData(mBannerOriginalData: MutableList<com.xianpeng.govass.fragment.working.BannerItem>): MutableList<BannerItem>? {
        var result: MutableList<BannerItem> = ArrayList()
        for (i in 0 until mBannerOriginalData.size) {
            var banner = BannerItem()
            banner.imgUrl = FILE_SERVER + mBannerOriginalData[i].imageUrl
            banner.title =
                mBannerOriginalData[i].contentType.toString() + mBannerOriginalData[i].title + "," + mBannerOriginalData[i].content
            result.add(banner)
        }
        return result
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

    override fun onDestroy() {
        super.onDestroy()
        ad_banner.recycle()
        EventBus.getDefault().unregister(this)
    }

    override fun OnSearchClick(keyword: String?) {
       if(!TextUtils.isEmpty(keyword)) {
           mViewModel.globalSearch(keyword)
       }
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

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) getUnReadMsgCountAndShow()
    }

    private fun getUnReadMsgCountAndShow() {
        AndroidNetworking.get(Constants.GET_UNREADMSG_COUNT)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build()
            .getAsObject(BaseResponse::class.java, object :
                ParsedRequestListener<BaseResponse> {
                override fun onResponse(response: BaseResponse?) {
                    if (response == null) {
                        toastError("获取未读消息数失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    BadgeView(context).bindTarget(titlebar).badgeNumber = response.data!!.toInt()
                }

                override fun onError(anError: ANError?) {
                    toastError("获取未读消息数失败，请稍后再试，msg=" + anError!!.errorDetail)
                }
            })
    }

    override fun onItemClick(position: Int) {
        when (position) {
            //反馈
            1 -> startActivity(
                Intent(
                    activity,
                    CommonListActivity::class.java
                ).putExtra("pageParam", FEED_BACK_HIS_PAGE)
            )
            //系统公告
            2 -> startActivity(
                Intent(
                    activity,
                    CommonListActivity::class.java
                ).putExtra("pageParam", SYSTEM_NOTICE_PAGE)
            )
            //调查问卷
            3 -> startActivity(
                Intent(
                    activity,
                    CommonListActivity::class.java
                ).putExtra("pageParam", QUESTION_NAIRE_PAGE)
            )
            //项目申报
            4 -> startActivity(
                Intent(
                    activity,
                    CommonListActivity::class.java
                ).putExtra("pageParam", PROJECT_DECLARE_PAGE)
            )
            else -> Log.e("zhangxianpeng", "无一命中")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: Msg?) {
        dismissLoading()
        if (event?.msg == MSGTYPE.POST_READ_PLAIN_MSG_SUCCESS.name) {
            getUnReadMsgCountAndShow()
        } else if(event?.msg == MSGTYPE.GET_GLOBAL_SEARCH_RESULT_SUCCESS.name) {
            var searchResultDta = event.searchResultDta
            showSearchResultDtaDialog(searchResultDta)
        }
    }

    /**
     * 展示全局搜索结果
     * @param searchResultDta 搜索结果
     */
    private fun showSearchResultDtaDialog(searchResultDta: List<GlobalSearchBean.GlobalSearchDta>?) {

    }
}