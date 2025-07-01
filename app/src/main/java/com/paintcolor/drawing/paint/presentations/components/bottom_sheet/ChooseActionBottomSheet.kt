package com.paintcolor.drawing.paint.presentations.components.bottom_sheet

import com.paintcolor.drawing.paint.base.BaseBottomSheetDialog
import com.paintcolor.drawing.paint.databinding.BottomSheetChooseSectionBinding
import com.paintcolor.drawing.paint.widget.tap

class ChooseActionBottomSheet(
    private val onSelectColoring: () -> Unit,
    private val onSelectDrawing: () -> Unit,
): BaseBottomSheetDialog<BottomSheetChooseSectionBinding>(BottomSheetChooseSectionBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            mcvColoring.tap {
                dismiss()
                onSelectColoring()
            }
            mcvDrawing.tap {
                dismiss()
                onSelectDrawing()
            }
        }
    }

    override fun dataCollect() {

    }

}