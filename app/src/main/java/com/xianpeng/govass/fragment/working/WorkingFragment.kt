package com.xianpeng.govass.fragment.working

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.tencent.mmkv.MMKV
import com.wyt.searchbox.SearchFragment
import com.wyt.searchbox.custom.IOnSearchClickListener
import com.xianpeng.govass.App
import com.xianpeng.govass.Constants
import com.xianpeng.govass.Constants.Companion.BANNER_PAGE
import com.xianpeng.govass.Constants.Companion.FILE_SERVER
import com.xianpeng.govass.R
import com.xianpeng.govass.activity.detailinfo.DetailInfoActivity
import com.xianpeng.govass.adapter.RecyclerViewBannerAdapter
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.util.CacheUtil
import com.xuexiang.xui.widget.actionbar.TitleBar
import com.xuexiang.xui.widget.banner.widget.banner.BannerItem
import com.xuexiang.xui.widget.textview.badge.BadgeView
import kotlinx.android.synthetic.main.fragment_working.*
import kotlinx.android.synthetic.main.layout_refresh_recycleview.*
import kotlinx.android.synthetic.main.titlebar_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class WorkingFragment : BaseFragment<BaseViewModel>(), IOnSearchClickListener {
    private val searchFragment by lazy { SearchFragment.newInstance() }
    private val page = 1
    private var mBannerOriginalData: MutableList<com.xianpeng.govass.fragment.working.BannerItem> =
        ArrayList()

    //消息列表
    private var mNormalMsgData: MutableList<NormalMsgItem> = ArrayList()
    private var msgAdapter: BaseQuickAdapter<NormalMsgItem, BaseViewHolder>? = null
    private var mBannerData: List<BannerItem>? = null

    var urls = arrayOf( //640*360 360/640=0.5625
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEMKAqaMvNAZl2TlD8UrDOKiFY.J6wfyK3oFZdj72OfWGZdxAJ*izVDHnN8VIR6mePwfGhnRC*8vzwAXSJ*XeAw0!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //党建进行时
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEAWtyxWv3gA*DuVXG7azyf*os6zhNqQwTYARQg2dn*IHISuQI2.UJEIDTYiVDq8Nreu4T0wRrzXPO80*Lx*7n9g!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //服务企业会客厅
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEEUE4yKjqRCqilKCBk78*KUjYTJifUgrCWYKIAdx2H2pUaXp.hEZLam0Nvy4k5h7O08yk0GQqYRJY2CjxTpD60o!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //系统公告
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrEDE8UAagar2fCb3mlx9rvhmkP1eP6t.5*gjvZcI3pW*01mZAv8HA328TaSlSMnXEQDmD*EU52W9mo1ZXOuREJUo!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4",  //问卷调查
        "http://m.qpic.cn/psc?/V13knR7n1SVrTL/TmEUgtj9EK6.7V8ajmQrENtQ9.y03w9xkMMD4DlbgYwbRBOesW.rOoM8qBNFmrwfX0xjSSHPEwuD3bh8Dt323jtH6yFCabBMDiyP.eVBHks!/b&bo=QAJcAQAAAAADFy0!&rf=viewer_4"  //项目申报
    )

    override fun layoutId(): Int = R.layout.fragment_working
    override fun initView(savedInstanceState: Bundle?) {
        BadgeView(context).bindTarget(titlebar).badgeNumber = CacheUtil.getUnReadCount()!!.toInt()
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
        titlebar.addAction(msgAction)
        searchFragment.setOnSearchClickListener(this)

        banner2.setAdapter(object : RecyclerViewBannerAdapter(urls) {})
        initMsgAdapter()
        initPageData()
    }

    private fun initMsgAdapter() {
        msgAdapter = object : BaseQuickAdapter<NormalMsgItem, BaseViewHolder>(
            R.layout.adapter_normalmsg_item,
            mNormalMsgData
        ) {
            override fun convert(holder: BaseViewHolder, item: NormalMsgItem) {
                holder.setText(R.id.tv_title, item.title)
            }
        }
        recycleview!!.layoutManager = LinearLayoutManager(App.instance)
        recycleview!!.adapter = msgAdapter
    }

    private fun initPageData() {
        getBannerData()
        getNormalMsgList()
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
                        return
                    }
                    mBannerOriginalData.addAll(response.data!!)
                    mBannerData = transData(mBannerOriginalData)
                    ad_banner.setSource(mBannerData)
                        .setOnItemClickListener { view: View?, t: BannerItem?, position: Int ->
                            startActivity(
                                Intent(
                                    activity,
                                    DetailInfoActivity::class.java
                                ).putExtra("pageParam", BANNER_PAGE)
                                    .putExtra("bannerContent", t?.title!!)
                            )
                        }
                        .setIsOnePageLoop(false).startScroll()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    private fun transData(mBannerOriginalData: MutableList<com.xianpeng.govass.fragment.working.BannerItem>): MutableList<BannerItem>? {
        var result: MutableList<BannerItem> = ArrayList()
        for (i in 0..(mBannerOriginalData.size - 1)) {
            var banner = BannerItem()
            banner.imgUrl = FILE_SERVER + mBannerOriginalData[i].imageUrl
            banner.title =
                mBannerOriginalData[i].contentType.toString() + mBannerOriginalData[i].title + "," + mBannerOriginalData[i].content
            result.add(banner)
        }
        return result
    }

    private fun getNormalMsgList() {
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
                    mNormalMsgData.addAll(response.data?.list!!)
                    if (msgAdapter != null) msgAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    object msgAction : TitleBar.Action {
        override fun leftPadding(): Int = 0
        override fun performAction(view: View?) {}
        override fun rightPadding(): Int = 0
        override fun getText(): String = ""
        override fun getDrawable(): Int = R.drawable.ic_baseline_message_24
    }

    override fun onDestroy() {
        super.onDestroy()
        ad_banner.recycle()
    }

    override fun OnSearchClick(keyword: String?) {
        TODO("Not yet implemented")
    }
}