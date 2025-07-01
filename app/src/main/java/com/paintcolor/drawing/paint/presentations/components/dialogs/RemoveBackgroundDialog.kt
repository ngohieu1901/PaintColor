package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogRemoveBackgroundBinding
import com.paintcolor.drawing.paint.widget.tap

class RemoveBackgroundDialog(
    private val onRemove: () -> Unit,
    private val onNotRemove: () -> Unit,
): BaseDialogFragment<DialogRemoveBackgroundBinding>(DialogRemoveBackgroundBinding::inflate) {
    override fun initData() {
        setCancelableCustom(false)
    }

    override fun setupView() {
        binding.apply {
            tvRemove.tap {
                dismiss()
                onRemove()
            }
            tvNo.tap {
                dismiss()
                onNotRemove()
            }
        }
    }

    override fun dataCollect() {

    }
}