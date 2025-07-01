package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogClearDrawingBinding
import com.paintcolor.drawing.paint.widget.tap

class ClearDrawingDialog(
    private val onClear : () -> Unit
): BaseDialogFragment<DialogClearDrawingBinding>(DialogClearDrawingBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvClear.tap {
                dismiss()
                onClear()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    override fun dataCollect() {

    }
}