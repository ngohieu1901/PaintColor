package com.paintcolor.drawing.paint.presentations.feature.color_palette.select.custom

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColorAddAndRecentBinding
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.toColorInt

class ColorAddAndRecentAdapter :
    BaseSyncDifferAdapter<String, ColorAddAndRecentAdapter.ColorAddAndRecentViewHolder>() {
    inner class ColorAddAndRecentViewHolder(binding: ItemColorAddAndRecentBinding) :
        BaseViewHolder<String, ItemColorAddAndRecentBinding>(binding) {
        override fun bindData(data: String) {
            super.bindData(data)
            if (data.isNotBlank()) {
                binding.viewColorCircle.background.setTint(data.toColorInt())
            }
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorAddAndRecentViewHolder =
        ColorAddAndRecentViewHolder(
            ItemColorAddAndRecentBinding.inflate(
                parent.layoutInflate(),
                parent,
                false
            )
        )

    override fun layoutResource(position: Int): Int = R.layout.item_color_add_and_recent

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}