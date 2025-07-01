package com.paintcolor.drawing.paint.base

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.paintcolor.drawing.paint.widget.tap

open class BaseViewHolder<M : Any, VB : ViewBinding>(val binding: VB) : RecyclerView.ViewHolder(binding.root) {
    protected val context: Context by lazy { binding.root.context }

    companion object {
        private var isItemClickable = false
    }

    @CallSuper
    open fun bindData(data: M) {
        binding.root.tap {
            if (!isItemClickable) { // chặn click nhiều item cùng lúc
                isItemClickable = true
                onItemClickListener(data)
                Handler(Looper.getMainLooper()).postDelayed({
                    isItemClickable = false
                }, 200)
            }
        }
    }

    open fun onItemClickListener(data: M) = Unit
}
