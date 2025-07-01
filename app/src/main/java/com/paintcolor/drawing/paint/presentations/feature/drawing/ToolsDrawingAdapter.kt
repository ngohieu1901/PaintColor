package com.paintcolor.drawing.paint.presentations.feature.drawing

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemToolDrawingBinding
import com.paintcolor.drawing.paint.domain.model.ToolsDrawing
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.visible

class ToolsDrawingAdapter(
    private val isDisableReward: Boolean,
    private val onSelectTools: (ToolsDrawing) -> Unit,
) : BaseSyncDifferAdapter<ToolsDrawing, ToolsDrawingAdapter.ToolsDrawingViewHolder>() {
    inner class ToolsDrawingViewHolder(binding: ItemToolDrawingBinding) :
        BaseViewHolder<ToolsDrawing, ItemToolDrawingBinding>(binding) {
        override fun bindData(data: ToolsDrawing) {
            super.bindData(data)
            binding.apply {
                tvName.text = context.getString(data.nameResource)
                ivIcon.setImageResource(data.iconResource)
                if (data.isViewedReward || isDisableReward) ivReward.gone() else ivReward.visible()
                if (data.isSelected) {
                    ivIcon.setColorFilter(context.getColor(R.color.white))
                    tvName.setTextColor(context.getColor(R.color.white))
                    llContainer.setBackgroundResource(R.drawable.bg_gradient_radius_8dp)
                } else {
                    ivIcon.setColorFilter(context.getColor(R.color.color_A1A1A1))
                    tvName.setTextColor(context.getColor(R.color.color_A1A1A1))
                    llContainer.setBackgroundResource(R.drawable.bg_transparent_no_stroke)
                }
            }
        }

        override fun onItemClickListener(data: ToolsDrawing) {
            super.onItemClickListener(data)
            onSelectTools(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ToolsDrawingViewHolder =
        ToolsDrawingViewHolder(
            ItemToolDrawingBinding.inflate(
                parent.layoutInflate(),
                parent,
                false
            )
        )

    override fun layoutResource(position: Int): Int = R.layout.item_tool_drawing

    override fun areContentsTheSame(oldItem: ToolsDrawing, newItem: ToolsDrawing): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: ToolsDrawing, newItem: ToolsDrawing): Boolean =
        oldItem.iconResource == newItem.iconResource
}