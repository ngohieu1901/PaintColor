package com.paintcolor.drawing.paint.presentations.feature.my_creation.sketch

import android.view.View
import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemSketchMyCreationBinding
import com.paintcolor.drawing.paint.domain.model.SketchMyCreation
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.loadImage
import com.paintcolor.drawing.paint.widget.loadThumbnail
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toDate
import com.paintcolor.drawing.paint.widget.visible

class MyCreationSketchAdapter(
    private val onOpenItem: (SketchMyCreation) -> Unit ,
    private val onClickMoreOption: (SketchMyCreation, View) -> Unit
) :
    BaseSyncDifferAdapter<SketchMyCreation, MyCreationSketchAdapter.MyCreationSketchVH>() {
    inner class MyCreationSketchVH(binding: ItemSketchMyCreationBinding) :
        BaseViewHolder<SketchMyCreation, ItemSketchMyCreationBinding>(binding) {
        override fun bindData(data: SketchMyCreation) {
            super.bindData(data)
            binding.apply {
                if (!data.isVideo) {
                    loadImage(ivDemo, data.filePath)
                    tvDuration.gone()
                } else {
                    loadThumbnail(ivDemo, data.filePath)
                    tvDuration.visible()
                    tvDuration.text = data.duration
                }
                tvName.text = data.filePath.substringAfterLast("/")
                tvModified.text = data.lastModified.toDate()
                ivMore.tap {
                    onClickMoreOption(data, ivMore)
                }
                if (bindingAdapterPosition == currentList.size - 1) {
                    vLine.gone()
                } else {
                    vLine.visible()
                }
            }
        }

        override fun onItemClickListener(data: SketchMyCreation) {
            super.onItemClickListener(data)
            onOpenItem(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): MyCreationSketchVH =
        MyCreationSketchVH(
            ItemSketchMyCreationBinding.inflate(
                parent.layoutInflate(),
                parent,
                false
            )
        )

    override fun layoutResource(position: Int): Int = R.layout.item_sketch_my_creation

    override fun areContentsTheSame(oldItem: SketchMyCreation, newItem: SketchMyCreation): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: SketchMyCreation, newItem: SketchMyCreation): Boolean =
        oldItem.filePath == newItem.filePath
}