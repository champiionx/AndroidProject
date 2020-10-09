package com.example.chinechat.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.chinechat.fragment.RecentFragment
import com.example.chinechat.fragment.UserFragment

class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return UserFragment()
            1 -> return RecentFragment()
        }
        return null!!
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when (position) {
            0 -> return "Users"
            1 -> return "Recent"
        }
        return null!!
    }
}