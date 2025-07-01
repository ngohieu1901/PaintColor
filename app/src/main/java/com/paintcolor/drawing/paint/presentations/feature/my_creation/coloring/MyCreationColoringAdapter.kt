package com.paintcolor.drawing.paint.presentations.feature.my_creation.coloring

import android.view.View
import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemColoringMyCreationBinding
import com.paintcolor.drawing.paint.domain.model.ColoringMyCreation
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.loadImageFromFile

class MyCreationColoringAdapter(
    private val onSelectImage: (ColoringMyCreation) -> Unit,
    private val onClickMoreOption: (ColoringMyCreation, view: View) -> Unit,
): BaseSyncDifferAdapter<ColoringMyCreation, MyCreationColoringAdapter.MyCreationColoringViewHolder>() {
    inner class MyCreationColoringViewHolder(binding: ItemColoringMyCreationBinding): BaseViewHolder<ColoringMyCreation, ItemColoringMyCreationBinding>(binding) {
        override fun bindData(data: ColoringMyCreation) {
            super.bindData(data)
            binding.ivImage.loadImageFromFile(data.imagePath)
            binding.ivMore.setOnClickListener {
                onClickMoreOption(data, binding.ivMore)
            }
        }

        override fun onItemClickListener(data: ColoringMyCreation) {
            super.onItemClickListener(data)
            onSelectImage(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): MyCreationColoringViewHolder = MyCreationColoringViewHolder(ItemColoringMyCreationBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_coloring_my_creation

    override fun areContentsTheSame(oldItem: ColoringMyCreation, newItem: ColoringMyCreation): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: ColoringMyCreation, newItem: ColoringMyCreation): Boolean = oldItem.imagePath == newItem.imagePath
}