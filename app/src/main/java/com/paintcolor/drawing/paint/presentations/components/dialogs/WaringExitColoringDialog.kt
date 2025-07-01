package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogWaringExitColoringBinding
import com.paintcolor.drawing.paint.widget.tap

class WaringExitColoringDialog(
    private val onExit: () -> Unit
): BaseDialogFragment<DialogWaringExitColoringBinding>(DialogWaringExitColoringBinding::inflate) {
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