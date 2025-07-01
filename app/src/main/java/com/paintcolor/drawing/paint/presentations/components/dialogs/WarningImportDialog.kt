package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogWarningImportBinding
import com.paintcolor.drawing.paint.widget.tap

class WarningImportDialog(
    private val onImport : () -> Unit
): BaseDialogFragment<DialogWarningImportBinding>(DialogWarningImportBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvContinue.tap {
                dismiss()
                onImport()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    override fun dataCollect() {

    }
}