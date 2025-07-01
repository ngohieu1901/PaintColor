package com.paintcolor.drawing.paint.presentations.feature.coloring

import android.view.ViewGroup
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseSyncDifferAdapter
import com.paintcolor.drawing.paint.base.BaseViewHolder
import com.paintcolor.drawing.paint.databinding.ItemTemplateBinding
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADED
import com.paintcolor.drawing.paint.domain.model.Template.Companion.DOWNLOADING
import com.paintcolor.drawing.paint.domain.model.Template.Companion.WAIT_FOR_DOWNLOAD
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.layoutInflate
import com.paintcolor.drawing.paint.widget.loadImage
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible

class TemplateAdapter(
    private val onSelectTemplate: (Template) -> Unit,
    private val onDownload: (Template) -> Unit,
) : BaseSyncDifferAdapter<Template, TemplateAdapter.TemplateVH>() {
    inner class TemplateVH(binding: ItemTemplateBinding) :
        BaseViewHolder<Template, ItemTemplateBinding>(binding) {
        override fun bindData(data: Template) {
            super.bindData(data)
            binding.apply {
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
                    onDownload(data)
                }
                when (data.stateDownload) {
                    WAIT_FOR_DOWNLOAD -> {
                        ivDownload.visible()
                        cpiDownload.gone()
                    }

                    DOWNLOADING -> {
                        ivDownload.gone()
                        cpiDownload.visible()
                    }

                    DOWNLOADED -> {
                        ivDownload.gone()
                        cpiDownload.gone()
                    }
                }
            }
        }

        override fun onItemClickListener(data: Template) {
            super.onItemClickListener(data)
            if (data.stateDownload == DOWNLOADED) onSelectTemplate(data)
        }
    }

    override fun createViewHolder(
        viewType: Int,
        parent: ViewGroup,
    ): TemplateAdapter.TemplateVH =
        TemplateVH(ItemTemplateBinding.inflate(parent.layoutInflate(), parent, false))

    override fun layoutResource(position: Int): Int = R.layout.item_template

    override fun areContentsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem == newItem

    override fun areItemsTheSame(oldItem: Template, newItem: Template): Boolean =
        oldItem.id == newItem.id
}