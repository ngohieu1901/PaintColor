package com.paintcolor.drawing.paint.presentations.feature.screen_base.language

import android.graphics.Color
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemLanguageBinding
import com.paintcolor.drawing.paint.domain.model.LanguageModel
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate

class LanguageAdapter(
    val onClick: (lang: LanguageModel) -> Unit
) : BaseSyncDifferAdapter<LanguageModel, LanguageAdapter.LanguageVH>()  {

    inner class LanguageVH(binding: ItemLanguageBinding) :
        BaseViewHolder<LanguageModel, ItemLanguageBinding>(binding) {
        override fun bindData(data: LanguageModel) {
            super.bindData(data)
            binding.apply {
                tvName.text = data.name

                if (data.active) {
                    layoutItem.setBackgroundResource(R.drawable.bg_item_language_select)
                    tvName.typeface = ResourcesCompat.getFont(binding.root.context, R.font.poppins_semibold)
                    tvName.setTextColor(Color.parseColor("#FFFFFF"))
                } else {
                    layoutItem.setBackgroundResource(R.drawable.bg_item_language)
                    tvName.typeface = ResourcesCompat.getFont(binding.root.context, R.font.poppins_semibold)
                    tvName.setTextColor(Color.parseColor("#374151"))
                }

                civLogo.apply {
                    when (data.code) {
                        "es" -> setImageResource(R.drawable.ic_span_flag)
                        "fr" -> setImageResource(R.drawable.ic_french_flag)
                        "hi" -> setImageResource(R.drawable.ic_hindi_flag)
                        "en" -> setImageResource(R.drawable.ic_english_flag)
                        "pt-rPT" -> setImageResource(R.drawable.ic_portuguese_flag)
                        "pt-rBR" -> setImageResource(R.drawable.ic_brazil_flag)
                        "ja" -> setImageResource(R.drawable.ic_japan_flag)
                        "de" -> setImageResource(R.drawable.ic_german_flag)
                        "zh-rCN" -> setImageResource(R.drawable.ic_china_flag)
                        "zh-rTW" -> setImageResource(R.drawable.ic_china_flag)
                        "ar" -> setImageResource(R.drawable.ic_a_rap_flag)
                        "bn" -> setImageResource(R.drawable.ic_bengali_flag)
                        "ru" -> setImageResource(R.drawable.ic_russia_flag)
                        "tr" -> setImageResource(R.drawable.ic_turkey_flag)
                        "ko" -> setImageResource(R.drawable.ic_korean_flag)
                        "in" -> setImageResource(R.drawable.flag_indonesia)
                    }
                }
            }
        }

        override fun onItemClickListener(data: LanguageModel) {
            super.onItemClickListener(data)
            onClick.invoke(data)
        }
    }

    fun setCheck(code: String) {
        val oldIndex = currentList.indexOfFirst { it.active }
        val newIndex = currentList.indexOfFirst { it.code == code }

        for (item in currentList) {
            item.active = item.code == code
        }

        if (oldIndex != -1) {
            notifyItemChanged(oldIndex)
        }
        notifyItemChanged(newIndex)
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): LanguageVH = LanguageVH(
        ItemLanguageBinding.inflate(parent.layoutInflate(), parent, false)
    )

    override fun layoutResource(position: Int): Int = R.layout.item_language

    override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean = oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean = oldItem == newItem
}