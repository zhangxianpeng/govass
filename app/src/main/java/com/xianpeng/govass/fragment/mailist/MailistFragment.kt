package com.xianpeng.govass.fragment.mailist

import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ExpandableListView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseFragment
import com.xianpeng.govass.ext.showMessage
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.visible
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet
import com.xuexiang.xui.widget.dialog.bottomsheet.BottomSheet.BottomListSheetBuilder
import com.xuexiang.xui.widget.dialog.materialdialog.DialogAction
import com.xuexiang.xui.widget.dialog.materialdialog.MaterialDialog
import kotlinx.android.synthetic.main.fragment_mailist.*
import kotlinx.android.synthetic.main.tab_title_layout.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel

/**
 * @description: 通讯录
 */
class MailistFragment : BaseFragment<BaseViewModel>() {

    private var groupArray: MutableList<GroupRes.GroupDataList.GroupData> = ArrayList()
    private var childArray: MutableList<ArrayList<ChildRes.UserInfo>> = ArrayList()
    private var userAdapter: ExpandableAdapter? = null

    private var isGetGoverMentUser = true

    override fun layoutId(): Int = R.layout.fragment_mailist

    override fun initView(savedInstanceState: Bundle?) {
        leftTabTv.setText("政府用户")
        rightTabTv.setText("企业用户")
        historyIv.visible(false)

        //政府用户点击
        ll_left.setOnClickListener {
            collapseGroup()
            isGetGoverMentUser = true
            title.text = "政府用户"
            showLoading()
            getGroupList()
            leftTabTv.setTypeface(null, Typeface.BOLD)
            left_indicator.visible(true)
            rightTabTv.setTypeface(null, Typeface.NORMAL)
            right_indicator.visible(false)
        }
        //企业用户点击
        ll_right.setOnClickListener {
            collapseGroup()
            isGetGoverMentUser = false
            title.text = "企业用户"
            showLoading()
            getGroupList()
            leftTabTv.setTypeface(null, Typeface.NORMAL)
            left_indicator.visible(false)
            rightTabTv.setTypeface(null, Typeface.BOLD)
            right_indicator.visible(true)
        }
        tv_send_msg.setOnClickListener {
            showBottomSheetListDialog(true,isGetGoverMentUser)
        }
        addIv.setOnClickListener {
            MaterialDialog.Builder(requireActivity())
                .customView(R.layout.dialog_custom, true)
                .title("新增分组")
                .positiveText("确定")
                .onPositive { dialog, which -> TODO("新增分组") }
                .negativeText("取消")
                .show()
        }

        initUserAdapter()
        showLoading()
        getGroupList()
    }

    private fun collapseGroup() {
        if (userAdapter!!.groupCount !== 0) {
            val count: Int = userAdapter!!.groupCount
            for (i in 0 until count) {
                user_listView.collapseGroup(i)
            }
        }
    }

    /**
     * 初始化用户列表适配器
     */
    private fun initUserAdapter() {
        userAdapter = ExpandableAdapter(requireActivity(), groupArray, childArray)
        user_listView.setAdapter(userAdapter)
        user_listView.setOnGroupClickListener { parent, view, groupPosition, id ->
            if (user_listView.isGroupExpanded(groupPosition)) {
                user_listView.collapseGroup(groupPosition)
            } else {
                user_listView.expandGroup(groupPosition, true)
                if (groupPosition == 0) {  //获取全部联系人
                    if (isGetGoverMentUser) getAllGoverMentUser() else getAllEnterpriseUser()
                } else {  //根据分组id获取联系人
                    if (isGetGoverMentUser) getGoverMentUserByGroupId(
                        groupArray!![groupPosition].id,
                        groupPosition
                    ) else getEnterpriseUserByGroupId(
                        groupArray!![groupPosition].id, groupPosition
                    )
                }
            }
            true
        }

        userAdapter!!.setOnElementClickListener(object : OnElementClickListener {
            override fun onElementClick(id: Int, elementId: Int) {
                when (elementId) {
                    R.id.tv_msg -> {
                        showBottomSheetListDialog(false,isGetGoverMentUser)
                    }
                    R.id.tv_delete -> {
                        showMessage("确定将此用户从当前分组中移除吗？", positiveAction = {
                            deleteUserFromCurrentGroup(id)
                        }, negativeButtonText = "取消")
                    }
                }
            }
        })

        user_listView.setOnChildClickListener { _, _, p2, p3, _ ->
            val contract = childArray[p2][p3].mobile
            showMessage("拨打：$contract", positiveAction = {
                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$contract"))
                startActivity(dialIntent)
            }, negativeButtonText = "取消")
            true
        }
    }

    /**
     * 底部弹出框
     */
    private fun showBottomSheetListDialog(isTiltleClick: Boolean,isGetGoverMentUser: Boolean) {
        if (isTiltleClick) {
            BottomListSheetBuilder(activity)
                .addItem("全员消息")
                .addItem("分组消息")
                .addItem("成员消息")
                .setIsCenter(true)
                .setOnSheetItemClickListener { dialog: BottomSheet, itemView: View?, position: Int, tag: String? ->
                    dialog.dismiss()
//                    XToastUtils.toast("Item " + (position + 1))
                }
                .build()
                .show()
        } else {
            // TODO: 2021/4/6  传入分组id 分组名称 
            BottomListSheetBuilder(activity)
                .setTitle("分组名称")
                .addItem("移除成员")
                .addItem("新增成员")
                .addItem("修改分组信息")
                .addItem("删除此分组")
                .setIsCenter(true)
                .setOnSheetItemClickListener { dialog: BottomSheet, itemView: View?, position: Int, tag: String? ->
                    dialog.dismiss()
//                    XToastUtils.toast("Item " + (position + 1))
                }
                .build()
                .show()
        }
    }

    private fun deleteUserFromCurrentGroup(id: Int) {

    }

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
                    groupArray.clear()
                    val titleGroup = GroupRes.GroupDataList.GroupData()
                    titleGroup.name = "全部联系人"
                    groupArray.add(titleGroup)
                    groupArray.addAll(response.data?.list!!)
                    for (i in groupArray.indices) {
                        val tempArray: ArrayList<ChildRes.UserInfo> = ArrayList()
                        childArray.add(tempArray)
                    }
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

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
                    val childModels = childArray[0]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    toastError(anError!!.errorDetail)
                }
            })
    }

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
                    val childModels = childArray[0]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }

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
                    val childModels = childArray[groupPosition]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
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
                    val childModels = childArray[groupPosition]
                    childModels.clear()
                    childModels.addAll(response.data!!)
                    if (userAdapter != null) userAdapter!!.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {
                    dismissLoading()
                    toastError(anError!!.errorDetail)
                }
            })
    }
}