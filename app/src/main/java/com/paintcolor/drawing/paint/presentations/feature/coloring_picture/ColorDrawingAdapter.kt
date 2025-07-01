package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemDrawingColorBinding
import com.paintcolor.drawing.paint.domain.model.SingleColor
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.toColorInt
import com.paintcolor.drawing.paint.widget.visible

class ColorDrawingAdapter(
    private val onSelectColor: (SingleColor) -> Unit
): BaseSyncDifferAdapter<SingleColor, ColorDrawingAdapter.ColorDrawingVH>() {
    inner class ColorDrawingVH(binding: ItemDrawingColorBinding): BaseViewHolder<SingleColor, ItemDrawingColorBinding>(binding){
        override fun bindData(data: SingleColor) {
            super.bindData(data)
            binding.apply {
                viewColorCircle.background.setTint(data.colorCode.toColorInt())
                if (data.isSelected) {
                    vSelect.visible()
                    ivSelect.visible()
                } else {
                    vSelect.gone()
                    ivSelect.gone()
                }
            }
        }

        override fun onItemClickListener(data: SingleColor) {
            super.onItemClickListener(data)
            if (!data.isSelected) onSelectColor(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorDrawingVH =
        ColorDrawingVH(ItemDrawingColorBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_drawing_color

    override fun areContentsTheSame(oldItem: SingleColor, newItem: SingleColor): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: SingleColor, newItem: SingleColor): Boolean = oldItem.id == newItem.id
}