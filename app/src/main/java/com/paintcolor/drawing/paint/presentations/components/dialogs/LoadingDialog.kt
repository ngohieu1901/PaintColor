package com.paintcolor.drawing.paint.presentations.components.dialogs

import android.content.Context
import android.view.LayoutInflater
import com.paintcolor.drawing.paint.base.BaseDialog
import com.paintcolor.drawing.paint.databinding.DialogLoadingBinding

class LoadingDialog(context: Context) : BaseDialog<DialogLoadingBinding>(context, false) {
    override fun initView() {

    }

    override fun initClickListener() {

    }

    override fun setViewBinding(
        inflater: LayoutInflater,
    ): DialogLoadingBinding {
        return DialogLoadingBinding.inflate(inflater)
    }
}