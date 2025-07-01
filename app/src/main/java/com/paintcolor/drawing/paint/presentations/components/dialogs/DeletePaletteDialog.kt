package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogDeletePaletteBinding
import com.paintcolor.drawing.paint.widget.tap

class DeletePaletteDialog(private val onDelete : () -> Unit
): BaseDialogFragment<DialogDeletePaletteBinding>(DialogDeletePaletteBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvDelete.tap {
                dismiss()
                onDelete()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    override fun dataCollect() {

    }
}