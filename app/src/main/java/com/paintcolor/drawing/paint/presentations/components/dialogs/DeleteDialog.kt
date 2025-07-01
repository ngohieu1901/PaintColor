package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogDeleteBinding
import com.paintcolor.drawing.paint.widget.tap

class DeleteDialog(
    private val onDelete : () -> Unit
): BaseDialogFragment<DialogDeleteBinding>(DialogDeleteBinding::inflate) {
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