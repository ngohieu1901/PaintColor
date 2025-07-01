package com.paintcolor.drawing.paint.presentations.components.bottom_sheet

import com.paintcolor.drawing.paint.base.BaseBottomSheetDialog
import com.paintcolor.drawing.paint.databinding.BottomSheetExitAppBinding
import com.paintcolor.drawing.paint.widget.tap

class ExitAppBottomSheet(
    private val onExit: () -> Unit
): BaseBottomSheetDialog<BottomSheetExitAppBinding>(BottomSheetExitAppBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvExit.tap {
                dismiss()
                onExit()
            }
            tvCancel.tap {
                dismiss()
            }
        }
    }

    override fun dataCollect() {

    }
}