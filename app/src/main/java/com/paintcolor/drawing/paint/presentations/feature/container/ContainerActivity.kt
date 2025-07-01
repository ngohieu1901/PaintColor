package com.paintcolor.drawing.paint.presentations.feature.container

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityContainerBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.bottom_sheet.ExitAppBottomSheet
import com.paintcolor.drawing.paint.presentations.components.dialogs.WaringExitColoringDialog
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.logEvent
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContainerActivity : BaseActivity<ActivityContainerBinding>() {
    private val viewmodel: ContainerViewModel by viewModel()

    private val sharePref: SharePrefUtils by inject()
    private val warningExitColoringDialog by lazy {
        WaringExitColoringDialog {
            popBackStack()
        }
    }

    private val exitAppBottomSheet by lazy {
        ExitAppBottomSheet {
            finishAffinity()
        }
    }

    companion object {
        var isOpenHome = false
    }

    override fun setViewBinding(): ActivityContainerBinding =
        ActivityContainerBinding.inflate(layoutInflater)

    override fun initView() {
        logEvent(EventName.home_open)
        if(!isOpenHome) sharePref.countOpenHome += 1
        if (sharePref.countOpenApp <= 10 && !isOpenHome) {
            isOpenHome = true
            logEvent(EventName.home_open + "_" + sharePref.countOpenApp)
        }

    }

    override fun onResume() {
        super.onResume()
        logEvent(EventName.home_view)
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewmodel.uiStateStore.collect {

                }
            }
        }
    }

    override fun onBackPressedSystem() {
        val navController = findNavController(R.id.fcv_app)
        val currentFragment = navController.currentDestination?.id
        when (currentFragment) {
            R.id.mainFragment -> {
                exitAppBottomSheet.show(supportFragmentManager, exitAppBottomSheet.tag)
            }

            R.id.coloringPictureFragment -> {
                warningExitColoringDialog.show(supportFragmentManager, warningExitColoringDialog.tag)
            }

            R.id.successfullyFragment -> {

            }

            else -> {
                popBackStack()
            }
        }
    }

    private fun popBackStack(
        destinationId: Int? = null,
        inclusive: Boolean = false
    ) {
        if (destinationId != null) {
            findNavController(R.id.fcv_app).popBackStack(destinationId, inclusive)
        } else {
            findNavController(R.id.fcv_app).popBackStack()
        }
    }
}