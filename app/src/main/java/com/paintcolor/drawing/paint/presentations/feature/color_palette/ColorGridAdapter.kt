package com.paintcolor.drawing.paint.presentations.feature.color_palette

import android.graphics.Color
import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColorCircleBinding
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.toColorInt
import com.paintcolor.drawing.paint.widget.visible

class ColorGridAdapter(
    private val onCLick : () -> Unit
): BaseSyncDifferAdapter<String, ColorGridAdapter.ColorCircleViewHolder>() {

    inner class ColorCircleViewHolder(binding: ItemColorCircleBinding): BaseViewHolder<String, ItemColorCircleBinding>(binding) {
        override fun bindData(data: String) {
            super.bindData(data)
            binding.apply {
                if (data.isNotBlank()) {
                    viewColorCircle.background.setTint(data.toColorInt())
                    viewStroke.gone()
                } else {
                    viewColorCircle.background.setTint(Color.WHITE)
                    viewStroke.visible()
                }
            }
        }

        override fun onItemClickListener(data: String) {
            onCLick()
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorCircleViewHolder = ColorCircleViewHolder(ItemColorCircleBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_color_circle

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem
}