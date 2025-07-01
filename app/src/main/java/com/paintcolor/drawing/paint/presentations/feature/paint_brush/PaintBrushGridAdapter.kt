package com.paintcolor.drawing.paint.presentations.feature.paint_brush

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemPainBrushGridBinding
import com.paintcolor.drawing.paint.domain.model.PaintBrush
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.visible

class PaintBrushGridAdapter(
    private val isDisableReward: Boolean,
    private val onSelectPaintBrush: (PaintBrush) -> Unit,
) : BaseSyncDifferAdapter<PaintBrush, PaintBrushGridAdapter.PaintBrushGridVH>() {
    inner class PaintBrushGridVH(binding: ItemPainBrushGridBinding) :
        BaseViewHolder<PaintBrush, ItemPainBrushGridBinding>(binding) {
        override fun bindData(data: PaintBrush) {
            super.bindData(data)
            binding.apply {
                if (data.isViewedReward || isDisableReward) ivReward.gone() else ivReward.visible()
                ivImage.setImageResource(data.imageLargeResource)
                tvName.text = context.getString(data.nameResource)
                mcvContainer.strokeColor = context.getColor(if (data.isSelected) R.color.app_color else R.color.white)
            }
        }

        override fun onItemClickListener(data: PaintBrush) {
            super.onItemClickListener(data)
            onSelectPaintBrush(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): PaintBrushGridVH =
        PaintBrushGridVH(ItemPainBrushGridBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_pain_brush_grid

    override fun areContentsTheSame(oldItem: PaintBrush, newItem: PaintBrush): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: PaintBrush, newItem: PaintBrush): Boolean =
        oldItem.nameResource == newItem.nameResource

}