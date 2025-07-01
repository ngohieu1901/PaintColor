package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColorCircleSmallBinding
import com.paintcolor.drawing.paint.domain.model.SingleColor
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.toColorInt

class ColorSmallAdapter: BaseSyncDifferAdapter<SingleColor, ColorSmallAdapter.ColorSmallViewHolder>() {
    inner class ColorSmallViewHolder(binding: ItemColorCircleSmallBinding): BaseViewHolder<SingleColor, ItemColorCircleSmallBinding>(binding){
        override fun bindData(data: SingleColor) {
            super.bindData(data)
            binding.colorDotView.background.setTint(data.colorCode.toColorInt())
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorSmallViewHolder = ColorSmallViewHolder(ItemColorCircleSmallBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_color_circle_small

    override fun areContentsTheSame(oldItem: SingleColor, newItem: SingleColor): Boolean = oldItem.id == newItem.id

    override fun areItemsTheSame(oldItem: SingleColor, newItem: SingleColor): Boolean = oldItem.id == newItem.id
}