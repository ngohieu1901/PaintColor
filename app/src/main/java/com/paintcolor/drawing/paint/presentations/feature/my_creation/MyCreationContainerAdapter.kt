package com.paintcolor.drawing.paint.presentations.feature.my_creation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.paintcolor.drawing.paint.presentations.feature.my_creation.coloring.MyCreationColoringFragment
import com.paintcolor.drawing.paint.presentations.feature.my_creation.sketch.MyCreationSketchFragment
import com.ironsource.re

class MyCreationContainerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0)  MyCreationColoringFragment() else MyCreationSketchFragment()
    }

}