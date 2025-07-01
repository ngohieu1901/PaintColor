package com.paintcolor.drawing.paint.presentations.components.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.databinding.BottomNavigationBinding
import com.paintcolor.drawing.paint.widget.tap

class BottomNavView(context: Context, attrs: AttributeSet): LinearLayout(context,attrs) {
    var binding: BottomNavigationBinding?= null
    var onBottomClick: ((String) -> Unit) ?= null

    companion object {
        const val COLORING_FRAGMENT = "COLORING_FRAGMENT"
        const val MY_CREATION_FRAGMENT = "MY_CREATION_FRAGMENT"
        const val SETTINGS_FRAGMENT = "SETTINGS_FRAGMENT"
    }

    init {
        binding = BottomNavigationBinding.inflate(LayoutInflater.from(context), this, true)
        binding?.apply {
            llColoring.tap {
                unSelectAllItem()
                ivColoring.setImageResource(R.drawable.ic_coloring_select)
                tvColoring.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                onBottomClick?.invoke(COLORING_FRAGMENT)
            }
            llMyCreation.tap {
                unSelectAllItem()
                ivMyCreation.setImageResource(R.drawable.ic_my_creation_select)
                tvMyCreation.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                onBottomClick?.invoke(MY_CREATION_FRAGMENT)
            }
            llSettings.tap {
                unSelectAllItem()
                ivSetting.setImageResource(R.drawable.ic_setting_select)
                tvSetting.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                onBottomClick?.invoke(SETTINGS_FRAGMENT)
            }
        }
    }

    fun setSelected(name: String) {
        binding!!.apply {
            unSelectAllItem()
            when (name) {
                COLORING_FRAGMENT -> {
                    ivColoring.setImageResource(R.drawable.ic_coloring_select)
                    tvColoring.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                }
                MY_CREATION_FRAGMENT -> {
                    ivMyCreation.setImageResource(R.drawable.ic_my_creation_select)
                    tvMyCreation.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                }
                SETTINGS_FRAGMENT -> {
                    ivSetting.setImageResource(R.drawable.ic_setting_select)
                    tvSetting.setTextColor(ContextCompat.getColor(context, R.color.app_color))
                }
            }
        }
    }

    private fun unSelectAllItem(){
        binding?.apply {
            ivColoring.setImageResource(R.drawable.ic_coloring)
            tvColoring.setTextColor(ContextCompat.getColor(context, R.color.text_not_select))
            ivMyCreation.setImageResource(R.drawable.ic_my_creation)
            tvMyCreation.setTextColor(ContextCompat.getColor(context, R.color.text_not_select))
            ivSetting.setImageResource(R.drawable.ic_settings)
            tvSetting.setTextColor(ContextCompat.getColor(context, R.color.text_not_select))
        }
    }
}