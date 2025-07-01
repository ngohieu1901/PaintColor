package com.paintcolor.drawing.paint.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.amazic.library.Utils.RemoteConfigHelper
import com.amazic.library.ads.admob.AdmobApi
import com.amazic.library.ads.callback.InterCallback
import com.amazic.library.ads.callback.RewardedCallback
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerBuilder
import com.amazic.library.ads.collapse_banner_ads.CollapseBannerManager
import com.amazic.library.ads.inter_ads.InterManager
import com.amazic.library.ads.native_ads.NativeBuilder
import com.amazic.library.ads.native_ads.NativeManager
import com.amazic.library.ads.reward_ads.RewardManager
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.utils.PermissionUtils
import com.paintcolor.drawing.paint.utils.SystemUtils.setLocale
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    protected val permissionUtils by lazy { PermissionUtils(requireActivity())}

    val exceptionHandler: CoroutineExceptionHandler by lazy { CoroutineExceptionHandler { _, exception ->
        Timber.e(exception, "${this::class.java.simpleName}: ${exception.message}")
    } }

    protected abstract fun initData()
    protected abstract fun setupView()
    protected abstract fun dataCollect()

    open fun hideSoftKeyboard() {
        activity?.currentFocus?.let {
            val inputMethodManager =
                activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(setLocale(context))
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        dataCollect()
    }

    @CallSuper
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun safeNavigate(
        @IdRes resId: Int,
        args: Bundle? = null,
    ) {
        findNavControllerOrNull()?.navigate(resId, args)
    }

    fun safeNavigate(
        navDirections: NavDirections
    ) {
        findNavControllerOrNull()?.navigate(navDirections)
    }

    private fun findNavControllerOrNull(): NavController? {
        return try {
            findNavController()
        } catch (e: Exception) {
            null
        }
    }

    fun safeNavigateParentNav(
        navDirections: NavDirections
    ) {
        findParentNavController().navigate(navDirections)
    }

    private fun findParentNavController(): NavController {
        return requireActivity().findNavController(R.id.fcv_app)
    }

    fun popBackStack(
        destinationId: Int? = null,
        inclusive: Boolean = false
    ) {
        findNavControllerOrNull()?.let {
            if (destinationId != null) {
                it.popBackStack(destinationId, inclusive)
            } else {
                it.popBackStack()
            }
        }
    }

    fun replaceFragment(id: Int, fragment: Fragment) {
        val ft: FragmentTransaction = parentFragmentManager.beginTransaction()
        ft.replace(id, fragment)
        ft.commit()
    }

    fun showPopupWindow(view: View, popupWindow: PopupWindow) {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val positionOfIcon = location[1]

        val displayMetrics = requireContext().resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3

        if (positionOfIcon > height) {
            popupWindow.showAsDropDown(view, -22, -(view.height * 7), Gravity.BOTTOM or Gravity.END)
        } else {
            popupWindow.showAsDropDown(view, -22, 0, Gravity.TOP or Gravity.END)
        }
    }

    protected fun showLoading() {
        (activity as? BaseActivity<*>)?.showLoading()
    }

    protected fun dismissLoading() {
        (activity as? BaseActivity<*>)?.dismissLoading()
    }

    protected fun renderLoading(isLoading: Boolean) {
        if (isLoading) showLoading() else dismissLoading()
    }

    protected fun renderError(error: Throwable) {
        toast(error.toString())
    }
}