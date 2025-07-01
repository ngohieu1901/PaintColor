package com.paintcolor.drawing.paint.presentations.feature.screen_base.intro

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.paintcolor.drawing.paint.databinding.ItemIntroAdsNativeBinding
import com.paintcolor.drawing.paint.databinding.ItemIntroBinding
import com.paintcolor.drawing.paint.domain.model.IntroModel

class IntroAdapter(
    private val context: AppCompatActivity,
    private val list: List<IntroModel> = emptyList(),
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class IntroDefaultVH(val binding: ItemIntroBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class IntroAdsNativeVH(val binding: ItemIntroAdsNativeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            IntroType.GUIDE_1.ordinal, IntroType.GUIDE_2.ordinal, IntroType.GUIDE_3.ordinal, IntroType.GUIDE_4.ordinal -> {
                val defaultBinding =
                    ItemIntroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                IntroDefaultVH(defaultBinding)
            }

            IntroType.ADS_1.ordinal, IntroType.ADS.ordinal -> {
                val adsNativeBinding = ItemIntroAdsNativeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                IntroAdsNativeVH(adsNativeBinding)
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return list[position].type.ordinal
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val introModel: IntroModel = list[position]
        when (holder.itemViewType) {
            IntroType.GUIDE_1.ordinal, IntroType.GUIDE_2.ordinal, IntroType.GUIDE_3.ordinal, IntroType.GUIDE_4.ordinal -> {
                val viewHolderDefault = holder as IntroDefaultVH
                viewHolderDefault.binding.apply {
                    ivIntro.setImageResource(introModel.image)
                    tvTitle.setText(introModel.title)
                    tvContent.setText(introModel.content)
                }
            }

            IntroType.ADS.ordinal -> {
                val viewHolderAdsNative = holder as IntroAdsNativeVH
            }

            IntroType.ADS_1.ordinal -> {
                val viewHolderAdsNative = holder as IntroAdsNativeVH
            }
        }
    }

}