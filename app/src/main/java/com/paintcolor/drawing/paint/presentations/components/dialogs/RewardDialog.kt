package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogRewardBinding
import com.paintcolor.drawing.paint.widget.tap

class RewardDialog(
    private val onWatchVideo: () -> Unit
): BaseDialogFragment<DialogRewardBinding>(DialogRewardBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.llWatch.tap {
            dismiss()
            onWatchVideo()
        }
    }

    override fun dataCollect() {

    }
}