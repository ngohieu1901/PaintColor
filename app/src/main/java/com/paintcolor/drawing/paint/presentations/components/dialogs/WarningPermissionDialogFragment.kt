package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogWarningPermissionBinding
import com.paintcolor.drawing.paint.widget.goToSetting
import com.paintcolor.drawing.paint.widget.tap

class WarningPermissionDialogFragment: BaseDialogFragment<DialogWarningPermissionBinding>(DialogWarningPermissionBinding::inflate) {
    override fun setupView() {
        binding.tvGoToSetting.tap {
           context?.goToSetting(requireActivity())
            dismiss()
        }
    }

    override fun initData() {
    }

    override fun dataCollect() {

    }
}