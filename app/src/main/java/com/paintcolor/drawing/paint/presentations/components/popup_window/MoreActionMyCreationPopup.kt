package com.paintcolor.drawing.paint.presentations.components.popup_window

import android.content.Context
import com.amazic.library.ads.admob.Admob
import com.paintcolor.drawing.paint.base.BasePopupWindow
import com.paintcolor.drawing.paint.databinding.PopupMoreActionMyCreationBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.tap

class MoreActionMyCreationPopup(
    context: Context,
    private val isNotUseEdit: Boolean,
    private val isViewedRewardShare: Boolean,
    private val isViewedRewardDownload: Boolean,
    private val onEdit: () -> Unit = {},
    private val onDownload: () -> Unit,
    private val onShare: () -> Unit,
    private val onDelete: () -> Unit,
) : BasePopupWindow<PopupMoreActionMyCreationBinding>(
    context,
    PopupMoreActionMyCreationBinding::inflate
) {
    init {
        binding.apply {
            val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_DOWNLOAD)
            if(isNotUseEdit) llEdit.gone()
            if(isViewedRewardShare || isDisableReward) ivRewardShare.gone()
            if(isViewedRewardDownload || isDisableReward) ivRewardDownload.gone()

            llEdit.tap {
                onEdit.invoke()
                dismiss()
            }
            llDownload.tap {
                onDownload.invoke()
                dismiss()
            }
            llShare.tap {
                onShare.invoke()
                dismiss()
            }
            llDelete.tap {
                onDelete.invoke()
                dismiss()
            }
        }
    }
}