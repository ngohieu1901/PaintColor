package com.paintcolor.drawing.paint.presentations.components.popup_window

import android.content.Context
import com.paintcolor.drawing.paint.base.BasePopupWindow
import com.paintcolor.drawing.paint.databinding.PopupMoreActionColorPaletteBinding
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.tap

class MoreActionColorPalettePopup(
    context: Context,
    private val isDisableDelete: Boolean,
    private val onEdit: () -> Unit,
    private val onDelete: () -> Unit
) : BasePopupWindow<PopupMoreActionColorPaletteBinding>(
    context,
    PopupMoreActionColorPaletteBinding::inflate
) {
    init {
        binding.apply {
            if (isDisableDelete) llDelete.gone()
            llDelete.tap {
                dismiss()
                onDelete()
            }
            llEdit.tap {
                dismiss()
                onEdit()
            }
        }
    }
}