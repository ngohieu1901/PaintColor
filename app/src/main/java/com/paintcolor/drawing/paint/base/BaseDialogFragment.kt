package com.paintcolor.drawing.paint.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.utils.PermissionUtils
import com.paintcolor.drawing.paint.utils.SystemUtils.setLocale
import com.paintcolor.drawing.paint.widget.hideNavigation
import com.paintcolor.drawing.paint.widget.hideStatusBar

abstract class BaseDialogFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB,
) : DialogFragment() {
    protected val permissionUtils by lazy { PermissionUtils(requireActivity()) }
    private var isCancelableCustom: Boolean = true

    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected abstract fun initData()
    protected abstract fun setupView()
    protected abstract fun dataCollect()

    fun setCancelableCustom(cancelable: Boolean): BaseDialogFragment<VB> {
        this.isCancelableCustom = cancelable
        return this
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = isCancelableCustom
        initData()
        dataCollect()
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = inflate(
        inflater,
        container,
        false,
    ).also { _binding = it }.root

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.hideNavigation()
        dialog?.window?.hideStatusBar()
        setupView()
    }

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    @CallSuper
    override fun onDetach() {
        try {
            dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDetach()
    }

    @CallSuper
    override fun onAttach(context: Context) {
        super.onAttach(setLocale(context))
    }

    override fun getTheme(): Int {
        return R.style.BaseDialog
    }
}