package com.xianpeng.govass.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gyf.immersionbar.ImmersionBar
import com.tencent.mmkv.MMKV
import com.xianpeng.govass.Constants
import com.xianpeng.govass.R
import com.xianpeng.govass.base.BaseActivity
import com.xianpeng.govass.ext.toastError
import com.xianpeng.govass.ext.toastNormal
import com.xianpeng.govass.ext.visible
import com.xianpeng.govass.fragment.dyment.DymentFragment
import com.xianpeng.govass.fragment.mailist.MailistFragment
import com.xianpeng.govass.fragment.mine.MineFragment
import com.xianpeng.govass.fragment.policy.PolicyFragment
import com.xianpeng.govass.fragment.working.WorkingFragment
import com.xianpeng.govass.util.CacheUtil
import kotlinx.android.synthetic.main.activity_main.*
import me.hgj.jetpackmvvm.base.viewmodel.BaseViewModel
import org.json.JSONObject

class MainActivity : BaseActivity<BaseViewModel>(),
    BottomNavigationView.OnNavigationItemSelectedListener {
    /**
     * 上次点击返回键的时间
     */
    private var lastBackPressTime = -1L

    private var supportFragmentTag = arrayOf(WORKING_TAG, DYMENT_TAG, MAILIST_TAG, POLICY_TAG, MINE_TAG)
    private var lastFragmentTag = ""

    override fun layoutId(): Int = R.layout.activity_main

    override fun initView(savedInstanceState: Bundle?) {
        navigation_launch.visible(CacheUtil.getUser()?.userType == 0)
        navigation_enter.visible(CacheUtil.getUser()?.userType != 0)
        // "内存重启"时调用
        if (savedInstanceState != null) {
            val fragmentList = supportFragmentManager.fragments
            val ft = supportFragmentManager.beginTransaction()
            for (fragment in fragmentList) {
                ft.hide(fragment)
            }
            ft.commitAllowingStateLoss()
        }
        initTab(savedInstanceState)
    }

    private fun initTab(savedInstanceState: Bundle?) {
        setDefaultTab()
        navigation_launch.setOnNavigationItemSelectedListener(this)
        navigation_enter.setOnNavigationItemSelectedListener(this)
    }

    private fun setDefaultTab() {
        switchToDestFragment(WORKING_TAG)
    }

    private fun switchToDestFragment(destTag: String) {
        if (destTag === lastFragmentTag) {
            return
        }
        if (!supportFragmentTag.contains(destTag)) {
            return
        }
        var fragment = supportFragmentManager.findFragmentByTag(destTag)
        val lastFragment = supportFragmentManager.findFragmentByTag(lastFragmentTag)
        lastFragmentTag = destTag
        val transaction = supportFragmentManager.beginTransaction()
        if (lastFragment != null) {
            transaction.hide(lastFragment)
        }
        if (fragment == null) {
            // 从未添加过
            fragment = getDestFragment(destTag)
            transaction.add(R.id.mainContent, fragment, destTag)
        } else {
            transaction.show(fragment)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun getDestFragment(destTag: String): Fragment {
        return when (destTag) {
            WORKING_TAG -> WorkingFragment()
            DYMENT_TAG -> DymentFragment()
            MAILIST_TAG -> MailistFragment()
            POLICY_TAG -> PolicyFragment()
            MINE_TAG -> MineFragment()
            else -> Fragment()  // do nothing
        }
    }


    override fun onBackPressed() {
        val currentTIme = System.currentTimeMillis()
        if (lastBackPressTime == -1L || currentTIme - lastBackPressTime >= 2000) {
            // 显示提示信息
            showBackPressTip()
            // 记录时间
            lastBackPressTime = currentTIme
        } else {
            //退出应用
            finish()
        }
    }

    private fun showBackPressTip() {
        toastNormal("再按一次退出")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        private const val WORKING_TAG = "working"
        private const val DYMENT_TAG = "dyment"
        private const val MAILIST_TAG = "mailist"
        private const val POLICY_TAG = "policy"
        private const val MINE_TAG = "mine"
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var itemId = item.itemId
        when (itemId) {
            R.id.navigation_working -> switchToDestFragment(WORKING_TAG)
            R.id.navigation_dyment -> switchToDestFragment(DYMENT_TAG)
            R.id.navigation_mailist -> switchToDestFragment(MAILIST_TAG)
            R.id.navigation_policy -> switchToDestFragment(POLICY_TAG)
            R.id.navigation_mine -> switchToDestFragment(MINE_TAG)
        }
        return true
    }
}