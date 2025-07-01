package com.paintcolor.drawing.paint.presentations.feature.custom_your_palette

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColorCustomBinding
import com.paintcolor.drawing.paint.domain.model.ColorCustom
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.toColorInt
import com.paintcolor.drawing.paint.widget.visible

class ColorCustomAdapter(
    private val onSelectColor: (ColorCustom) -> Unit,
    private val onAddColor: (ColorCustom) -> Unit,
) : BaseSyncDifferAdapter<ColorCustom, ColorCustomAdapter.ColorCustomVH>() {
    inner class ColorCustomVH(binding: ItemColorCustomBinding) :
        BaseViewHolder<ColorCustom, ItemColorCustomBinding>(binding) {
        override fun bindData(data: ColorCustom) {
            super.bindData(data)
            binding.apply {
                vColorCircle.background.setTint(data.colorCode.toColorInt())

                if (data.colorCode.isBlank()) {
                    vStrokeGray.visible()
                } else {
                    vStrokeGray.gone()
                }

                if (data.isItemSelected) {
                    vSelect.visible()
                } else {
                    vSelect.gone()
                }

                if (data.isItemAdd) {
                    ivAdd.visible()
                } else {
                    ivAdd.gone()
                }
            }
        }

        override fun onItemClickListener(data: ColorCustom) {
            super.onItemClickListener(data)
            binding.apply {
                if (data.isItemAdd) {
                    onAddColor(data)
                } else {
                    if (data.colorCode.isNotBlank())
                        onSelectColor(data)
                }
            }
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorCustomVH =
        ColorCustomVH(ItemColorCustomBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_color_custom

    override fun areContentsTheSame(oldItem: ColorCustom, newItem: ColorCustom): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: ColorCustom, newItem: ColorCustom): Boolean = oldItem.id == newItem.id
}