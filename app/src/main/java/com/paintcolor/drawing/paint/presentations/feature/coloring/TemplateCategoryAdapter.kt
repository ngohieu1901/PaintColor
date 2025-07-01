package com.paintcolor.drawing.paint.presentations.feature.coloring

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseAsyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemCategoryTemplateBinding
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.TemplateCategory
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.tap

class TemplateCategoryAdapter(
    private val onSelect: (Template) -> Unit,
    private val onDownload: (Template) -> Unit,
    private val onOpenCategory: (String) -> Unit,
): BaseAsyncDifferAdapter<TemplateCategory, TemplateCategoryAdapter.TemplateCategoryViewHolder>() {

    inner class TemplateCategoryViewHolder(binding: ItemCategoryTemplateBinding): BaseViewHolder<TemplateCategory, ItemCategoryTemplateBinding>(binding) {
        override fun bindData(data: TemplateCategory) {
            super.bindData(data)
            binding.apply {
                tvName.text = data.nameCategory
                tvSeeAll.tap {
                    onOpenCategory(data.nameCategory)
                }
                rvListImage.adapter =
                    TemplateAdapter(onSelect, onDownload).apply { submitList(data.listTemplate) }
            }
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): TemplateCategoryViewHolder = TemplateCategoryViewHolder(ItemCategoryTemplateBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_category_template

    override fun areContentsTheSame(oldItem: TemplateCategory, newItem: TemplateCategory): Boolean = oldItem == newItem

    override fun areItemsTheSame(oldItem: TemplateCategory, newItem: TemplateCategory): Boolean = oldItem.idCategory == newItem.idCategory
}