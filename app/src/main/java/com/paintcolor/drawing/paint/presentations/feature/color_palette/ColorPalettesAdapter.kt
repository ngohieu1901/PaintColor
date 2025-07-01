package com.paintcolor.drawing.paint.presentations.feature.color_palette

import android.view.View
import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseAsyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColorPaletteBinding
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.widget.invisible
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible

class ColorPalettesAdapter(
    private val isShowDotMore: Boolean,
    private val onSelectPalette: (ColorPalette, Int) -> Unit,
    private val onClickMoreOption: (ColorPalette, View) -> Unit = { _, _ -> },
): BaseAsyncDifferAdapter<ColorPalette, ColorPalettesAdapter.ColorPaletteViewHolder>() {

    inner class ColorPaletteViewHolder(binding: ItemColorPaletteBinding): BaseViewHolder<ColorPalette, ItemColorPaletteBinding>(binding) {
        override fun bindData(data: ColorPalette) {
            super.bindData(data)
            binding.apply {
                if (isShowDotMore) ivMore.visible() else ivMore.invisible()
                rvColor.adapter = ColorGridAdapter {
                    onSelectPalette(data, bindingAdapterPosition)
                }.apply { addList(data.listColor) }
                ivMore.tap {
                    onClickMoreOption(data, ivMore)
                }
                if (data.nameString.isNotBlank()) {
                    tvName.text = data.nameString
                } else {
                    tvName.text = context.getString(data.nameResource)
                }
                mcvContainer.strokeColor = context.getColor(if (data.isSelected) R.color.app_color else R.color.white)
            }
        }

        override fun onItemClickListener(data: ColorPalette) {
            onSelectPalette(data, bindingAdapterPosition)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): ColorPaletteViewHolder =
        ColorPaletteViewHolder(
            ItemColorPaletteBinding.inflate(
                parent.layoutInflate(),
                parent,
                false
            )
        )

    override fun layoutResource(position: Int): Int = R.layout.item_color_palette

    override fun areContentsTheSame(
        oldItem: ColorPalette,
        newItem: ColorPalette
    ): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: ColorPalette, newItem: ColorPalette): Boolean =
        oldItem.id == newItem.id
}