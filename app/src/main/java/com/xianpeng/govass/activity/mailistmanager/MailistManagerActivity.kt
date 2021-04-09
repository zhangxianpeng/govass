package com.xianpeng.govass.activity.mailistmanager

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.adapter.GroupManagerAdapter
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.fragment.mailist.ChildRes
import com.xianpeng.govass.fragment.mailist.GroupRes
import kotlinx.android.synthetic.main.activity_mailist_manager.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

class MailistManagerActivity : BaseActivity<BaseViewModel>() {
    private var memberAdapter: GroupManagerAdapter? = null
    private var mData: MutableList<MemberItemBean> = ArrayList()
    private var isGetGoverMentUser = false
    override fun layoutId(): Int = R.layout.activity_mailist_manager

    override fun initView(savedInstanceState: Bundle?) {
        titlebar.setLeftClickListener { finish() }
        val flagType = intent.getStringExtra("flagType")
        val itemPosition = intent.getIntExtra("itemPosition", -1)
        isGetGoverMentUser = intent.getBooleanExtra("isGetGoverMentUser",false)
        if (flagType == "plainMsg") {
            when (itemPosition) {
                1 -> {
                    titlebar.setTitle("选择分组发送消息")
                    getGroupList()
                }
                2 -> {
                    titlebar.setTitle("选择成员发送消息")
                    if(isGetGoverMentUser) getAllGoverMentUser() else getAllEnterpriseUser()
                }
            }
        } else {
            when (itemPosition) {
                0 -> {
                    titlebar.setTitle("从当前分组移除成员")
                }
                1 -> {
                    titlebar.setTitle("添加成员到当前分组")
                }
            }
        }
        initAdapter()
    }

    private fun initAdapter() {
        memberAdapter = GroupManagerAdapter(this, mData)
        recycleview.layoutManager = LinearLayoutManager(this)
        recycleview.adapter = memberAdapter
    }

    private fun transferGroupData(data: ArrayList<GroupRes.GroupDataList.GroupData>?): Collection<MemberItemBean> {
        val result:MutableList<MemberItemBean> = ArrayList()
        for(i in 0 until data!!.size) {
            val item = MemberItemBean()
            item.name = data[i].name
            item.id = data[i].id
            result.add(item)
        }
        return result
    }

    private fun transferMemberData(data: List<ChildRes.UserInfo>): Collection<MemberItemBean> {
        val result:MutableList<MemberItemBean> = ArrayList()
        for(i in data.indices) {
            val item = MemberItemBean()
            item.name = data[i].username + "-" + data[i].enterpriseName
            item.id = data[i].id
            result.add(item)
        }
        return result
    }

    /**
     * 获取分组列表
     */
    private fun getGroupList() {
        AndroidNetworking.get(Constants.GET_ALL_GROUP)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("type", if (isGetGoverMentUser) "0" else "1")
            .build().getAsObject(GroupRes::class.java, object :
                ParsedRequestListener<GroupRes> {
                override fun onResponse(response: GroupRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取分组列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg!!)
                        return
                    }
                    mData.clear()
                    mData.addAll(transferGroupData(response.data!!.list))
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 获取全部政府人员
     */
    private fun getAllGoverMentUser() {
        AndroidNetworking.get(Constants.GET_ALL_GOVERMENT_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    mData.clear()
                    mData.addAll(transferMemberData(response.data!!))
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 获取全部企业用户
     */
    private fun getAllEnterpriseUser() {
        AndroidNetworking.get(Constants.GET_ALL_ENTERPRISE_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    /**
     * 根据分组id获取分组成员
     */
    private fun getGoverMentUserByGroupId(groupId: Int, groupPosition: Int) {
        AndroidNetworking.get(Constants.GET_ALL_GOVERMENT_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("groupId", groupId.toString())
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }

                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

    private fun getEnterpriseUserByGroupId(groupId: Int, groupPosition: Int) {
        AndroidNetworking.get(Constants.GET_ALL_ENTERPRISE_USER)
            .addHeaders("token", MMKV.defaultMMKV().getString("loginToken", ""))
            .addQueryParameter("groupId", groupId.toString())
            .build().getAsObject(ChildRes::class.java, object :
                ParsedRequestListener<ChildRes> {
                override fun onResponse(response: ChildRes?) {
                    dismissLoading()
                    if (response == null) {
                        toastError("获取通讯录列表失败，请稍后再试")
                        return
                    }
                    if (response.code != 0) {
                        toastError(response.msg)
                        return
                    }
                    if (memberAdapter != null) memberAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }
}