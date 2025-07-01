package com.paintcolor.drawing.paint.presentations.feature.screen_base.uninstall

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemAnswerBinding
import com.paintcolor.drawing.paint.domain.model.AnswerModel
import com.paintcolor.drawing.paint.widget.layoutInflate

class UninstallAdapter(private val onClick: (AnswerModel, position: Int) -> Unit): BaseSyncDifferAdapter<AnswerModel, UninstallAdapter.AnswerVH>() {
    inner class AnswerVH(binding: ItemAnswerBinding): BaseViewHolder<AnswerModel, ItemAnswerBinding>(binding){
        override fun bindData(data: AnswerModel) {
            super.bindData(data)
            binding.apply {
                tvName.text = context.getString(data.name)
                if (data.isSelected) {
                    rdSelect.setImageResource(R.drawable.rd_select_why)
                } else {
                    rdSelect.setImageResource(R.drawable.rd_un_select_why)
                }
            }
        }

        override fun onItemClickListener(data: AnswerModel) {
            super.onItemClickListener(data)
            onClick(data.copy(isSelected = true), bindingAdapterPosition)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): AnswerVH {
        return AnswerVH(ItemAnswerBinding.inflate(parent.layoutInflate(), parent,false))
    }

    override fun layoutResource(position: Int): Int {
        return R.layout.item_answer
    }

    override fun areItemsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: AnswerModel, newItem: AnswerModel): Boolean = oldItem == newItem
}