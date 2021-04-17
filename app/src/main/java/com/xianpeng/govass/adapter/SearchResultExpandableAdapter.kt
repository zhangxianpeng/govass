package com.xianpeng.govass.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.xianpeng.govass.R
import com.xianpeng.govass.fragment.working.GlobalSearchBean
import com.xianpeng.govass.fragment.working.GlobalSearchGroupDataBean
import de.hdodenhof.circleimageview.CircleImageView

class SearchResultExpandableAdapter(context: Context, groupData: List<GlobalSearchGroupDataBean>?, childData: List<List<GlobalSearchBean.GlobalSearchBeanDetail.GlobalSearchDta>>?) : BaseExpandableListAdapter() {
    private var mContext: Context = context
    private var mGroupArray: List<GlobalSearchGroupDataBean>? = groupData
    private var mChildArray: List<List<GlobalSearchBean.GlobalSearchBeanDetail.GlobalSearchDta>>? = childData

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return mChildArray!![groupPosition][childPosition]
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    @SuppressLint("SetTextI18n")
    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = convertView
        val groupHolder: ViewHolderGroup
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_searchresult_group_item, parent, false)
            groupHolder =
                ViewHolderGroup()
            groupHolder.tv_group_name = convertView!!.findViewById<View>(R.id.tv_group_name) as TextView?  //data.getTitle()
            groupHolder.tv_manager = convertView.findViewById<View>(R.id.check_more) as TextView?   //data.getOnline()
            convertView.tag = groupHolder
        } else {
            groupHolder = convertView.tag as ViewHolderGroup
        }
        groupHolder.tv_group_name?.text = mGroupArray!![groupPosition].type
        groupHolder.tv_manager?.text = "查看更多（" + mGroupArray!![groupPosition].cocunt + ")"
        return convertView
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val itemHolder: ViewHolderItem
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_searchresult_child_item, parent, false)
            itemHolder =
                ViewHolderItem()
            itemHolder.tv_username = convertView.findViewById<View>(R.id.tv_equal_content) as TextView?
            itemHolder.tv_delete = convertView.findViewById<View>(R.id.tv_source) as TextView?
            convertView.tag = itemHolder
        } else {
            itemHolder = convertView.tag as ViewHolderItem
        }

        val content = mChildArray!![groupPosition][childPosition].content
        var source =""
        when(mChildArray!![groupPosition][childPosition].type) {
            0-> source = "千企动态"
            1-> source ="系统公告"
            2-> source ="政策文件库"
        }
        itemHolder.tv_username!!.text = content
        itemHolder.tv_delete!!.text = source
        itemHolder.tv_username!!.setOnClickListener {
            //todo 全局搜索点击事件
        }
        return convertView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return mChildArray!![groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return mGroupArray!![groupPosition]
    }

    override fun getGroupCount(): Int {
        return mGroupArray!!.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    private class ViewHolderGroup {
        var tv_group_name: TextView? = null
        var tv_manager: TextView? = null
        var img_indicator: ImageView? = null
    }

    private class ViewHolderItem {
        var img_head: CircleImageView? = null
        var tv_username: TextView? = null
        var tv_delete: TextView? = null
    }
}

