package com.paintcolor.drawing.paint.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.google.android.play.core.install.model.InstallStatus
import com.paintcolor.drawing.paint.base.network.NetworkCallbackHandler
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SCREEN
import com.paintcolor.drawing.paint.constants.Constants.IntentKeys.SPLASH_ACTIVITY
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.presentations.components.dialogs.LoadingDialog
import com.paintcolor.drawing.paint.presentations.feature.screen_base.no_internet.NoInternetActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity.Companion.appUpdateManager
import com.paintcolor.drawing.paint.presentations.feature.screen_base.splash.SplashActivity.Companion.installStateUpdatedListener
import com.paintcolor.drawing.paint.utils.PermissionUtils
import com.paintcolor.drawing.paint.utils.SystemUtils.setLocale
import com.paintcolor.drawing.paint.widget.currentBundle
import com.paintcolor.drawing.paint.widget.hideNavigation
import com.paintcolor.drawing.paint.widget.hideStatusBar
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class BaseActivity<VB : ViewBinding>() : AppCompatActivity() {
    protected lateinit var binding: VB
    private var isRegistered = false
    private var networkCallback: NetworkCallbackHandler? = null

    protected val permissionUtils by lazy { PermissionUtils(this)}

    protected val exceptionHandler: CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, exception ->
            Timber.e(exception, "${this::class.java.simpleName}: ${exception.message}")
        }
    }

    protected abstract fun setViewBinding(): VB
    private val loadingDialog by lazy { LoadingDialog(this) }
    protected abstract fun initView()
    protected abstract fun dataCollect()

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.hideNavigation()
        window.hideStatusBar()
        super.onCreate(savedInstanceState)
        binding = setViewBinding()
        //onBackPress
        onBackPressedDispatcher.addCallback(this) {
            isEnabled = false
            onBackPressedSystem()
            isEnabled = true
        }
        setContentView(binding.root)
        //internet
        networkCallback = NetworkCallbackHandler {
            if (!it) {
                if (this !is NoInternetActivity) {
                    launchActivity(NoInternetActivity::class.java)
                }
            } else {
                if (this is NoInternetActivity && this.currentBundle()
                        ?.getString(SCREEN) != SPLASH_ACTIVITY
                ) {
                    finish()
                } else if (this is NoInternetActivity && this.currentBundle()
                        ?.getString(SCREEN) == SPLASH_ACTIVITY
                ) {
                    val myIntent = Intent(this, SplashActivity::class.java)
                    myIntent.flags =
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(myIntent)
                    finishAffinity()
                }
            }
        }
        networkCallback?.register(this)
        initView()
        dataCollect()
    }

    override fun onResume() {
        super.onResume()
        window.hideStatusBar()
        window.hideNavigation()
        AdsHelper.enableResume(this)
        installStateUpdatedListener?.let { appUpdateManager?.registerListener(it) }
        appUpdateManager?.appUpdateInfo?.addOnSuccessListener { appUpdateInfo ->
            // If the update is downloaded but not installed,
            // notify the user to complete the update.
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                appUpdateManager?.completeUpdate()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        networkCallback?.unregister()
        installStateUpdatedListener?.let { appUpdateManager?.unregisterListener(it) }
    }

    open fun onBackPressedSystem() {
        onBackPressedDispatcher.onBackPressed()
    }

    protected fun showPopupWindow(view: View, popupWindow: PopupWindow) {
        val location = IntArray(2)
        view.getLocationInWindow(location)
        val positionOfIcon = location[1]

        val displayMetrics = resources.displayMetrics
        val height = displayMetrics.heightPixels * 2 / 3

        if (positionOfIcon > height) {
            popupWindow.showAsDropDown(view, -22, -(view.height * 7), Gravity.BOTTOM or Gravity.END)
        } else {
            popupWindow.showAsDropDown(view, -22, 0, Gravity.TOP or Gravity.END)
        }
    }

    fun showLoading() {
        if (loadingDialog.isShowing.not())
            loadingDialog.show()
    }

    fun dismissLoading() {
        if (loadingDialog.isShowing) loadingDialog.dismiss()

    }

    protected suspend fun renderLoading(isLoading: Boolean) {
        if (isLoading) showLoading() else dismissLoading()
    }

    protected fun renderError(error: Throwable) {
        toast(error.toString())
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase?.let { setLocale(it) })
    }
}