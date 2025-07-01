package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemDrawingPaintBrushBinding
import com.paintcolor.drawing.paint.domain.model.PaintBrush
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.visible

class PaintBrushDrawingAdapter(
    private val isDisableReward: Boolean,
    private val onSelectPaintBrush: (PaintBrush, Int) -> Unit,
) : BaseSyncDifferAdapter<PaintBrush, PaintBrushDrawingAdapter.PaintBrushVH>() {
    inner class PaintBrushVH(binding: ItemDrawingPaintBrushBinding) :
        BaseViewHolder<PaintBrush, ItemDrawingPaintBrushBinding>(binding) {
        override fun bindData(data: PaintBrush) {
            super.bindData(data)
            binding.apply {
                if (data.isViewedReward || isDisableReward) ivReward.gone() else ivReward.visible()
                civLogo.setImageResource(data.imageResource)
                tvName.text = context.getString(data.nameResource)
                civLogo.borderWidth = if (data.isSelected) 2 else 0
            }
        }

        override fun onItemClickListener(data: PaintBrush) {
            super.onItemClickListener(data)
            onSelectPaintBrush(data, bindingAdapterPosition)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): PaintBrushVH =
        PaintBrushVH(ItemDrawingPaintBrushBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_drawing_paint_brush

    override fun areContentsTheSame(oldItem: PaintBrush, newItem: PaintBrush): Boolean =
        oldItem.isSelected == newItem.isSelected

    override fun areItemsTheSame(oldItem: PaintBrush, newItem: PaintBrush): Boolean =
        oldItem.nameResource == newItem.nameResource
}