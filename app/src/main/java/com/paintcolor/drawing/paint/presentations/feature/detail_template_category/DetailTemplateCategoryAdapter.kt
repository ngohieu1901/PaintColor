package com.paintcolor.drawing.paint.presentations.feature.detail_template_category

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemTemplateGridBinding
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADED
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADING
import com.paintcolor.drawing.paint.domain.model.Template.Companion.WAIT_FOR_DOWNLOAD
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.loadImage
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible

class DetailTemplateCategoryAdapter(
    private val isDisableReward: Boolean,
    private val onSelectTemplate: (Template) -> Unit,
    private val onDownloadTemplate: (Template) -> Unit,
) : BaseSyncDifferAdapter<Template, DetailTemplateCategoryAdapter.TemplateVH>() {
    inner class TemplateVH(binding: ItemTemplateGridBinding) :
        BaseViewHolder<Template, ItemTemplateGridBinding>(binding) {
        override fun bindData(data: Template) {
            super.bindData(data)
            binding.apply {
                binding.apply {
                    if (data.isViewedReward || isDisableReward) ivReward.gone() else ivReward.visible()

                    loadImage(
                        imageView = ivData,
                        url = data.imagePath,
                        onShowLoading = {
                            cpiLoadTemplate.visible()
                        },
                        onDismissLoading = {
                            cpiLoadTemplate.gone()
                        })

                    ivDownload.tap {
                        onDownloadTemplate(data)
                    }

                    when (data.stateDownload) {
                        WAIT_FOR_DOWNLOAD -> {
                            ivDownload.visible()
                            cpiLoading.gone()
                        }

                        DOWNLOADING -> {
                            ivDownload.gone()
                            cpiLoading.visible()
                        }

                        DOWNLOADED -> {
                            ivDownload.gone()
                            cpiLoading.gone()
                        }
                    }
                }
            }
        }

        override fun onItemClickListener(data: Template) {
            super.onItemClickListener(data)
            if (data.stateDownload == DOWNLOADED) onSelectTemplate(data)
        }
    }

    override fun createViewHolder(viewType: Int, parent: ViewGroup): TemplateVH =
        TemplateVH(ItemTemplateGridBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_template_grid

    override fun areContentsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem.imagePath == newItem.imagePath
}