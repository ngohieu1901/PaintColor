package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogCongratulationBinding
import com.paintcolor.drawing.paint.widget.tap

class CongratulationDialog: BaseDialogFragment<DialogCongratulationBinding>(DialogCongratulationBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvOk.tap {
                dismiss()
            }
        }
    }

    override fun dataCollect() {

    }
}