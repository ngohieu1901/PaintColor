package com.paintcolor.drawing.paint.presentations.components.dialogs

import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogUnlockAllRewardBinding
import com.paintcolor.drawing.paint.widget.tap

class UnlockAllRewardDialog(
    private val videoWatched: Int = 0,
    private val onWatchVideo: () -> Unit
): BaseDialogFragment<DialogUnlockAllRewardBinding>(DialogUnlockAllRewardBinding::inflate) {
    override fun initData() {

    }

    override fun setupView() {
        binding.apply {
            tvCurrentCountWatched.text = videoWatched.toString()
            when(videoWatched) {
                0 -> {
                    progressBar.progress = 0
                }
                1 -> {
                    progressBar.progress = 50
                }
                2 -> {
                    progressBar.progress = 100
                }
            }
            llWatch.tap {
                dismiss()
                onWatchVideo()
            }
        }
    }

    override fun dataCollect() {

    }
}