package com.paintcolor.drawing.paint.presentations.feature.interest

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemInterestBinding
import com.paintcolor.drawing.paint.domain.model.InterestModel
import com.paintcolor.drawing.paint.widget.layoutInflate

class InterestAdapter(
    private val onSelectItem: (InterestModel) -> Unit
): BaseSyncDifferAdapter<InterestModel, InterestAdapter.InterestViewHolder>() {
    inner class InterestViewHolder(binding: ItemInterestBinding): BaseViewHolder<InterestModel, ItemInterestBinding>(binding) {
        override fun bindData(data: InterestModel) {
            super.bindData(data)
            binding.apply {
                ivImage.setImageResource(data.imageResource)
                tvName.setText(data.nameResource)
                mcvContainer.strokeColor = context.getColor(if (data.isSelected) R.color.app_color else R.color.white)
            }
        }

        override fun onItemClickListener(data: InterestModel) {
            super.onItemClickListener(data)
            onSelectItem(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): InterestViewHolder = InterestViewHolder(
        ItemInterestBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_interest

    override fun areContentsTheSame(oldItem: InterestModel, newItem: InterestModel): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: InterestModel, newItem: InterestModel): Boolean = oldItem.nameResource == newItem.nameResource
}