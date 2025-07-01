package com.paintcolor.drawing.paint.presentations.feature.screen_base.permission

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.databinding.ActivityPermissionBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningPermissionDialogFragment
import com.paintcolor.drawing.paint.presentations.feature.color_palette.start.ColorPaletteStartActivity
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.presentations.feature.interest.InterestActivity
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.callMultiplePermissions
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class PermissionActivity : BaseActivity<ActivityPermissionBinding>() {
    private val callStoragePermission = callMultiplePermissions {
        binding.ivSwitch.isChecked = true
        binding.ivSwitch.isEnabled = false
    }

    private val sharePref: SharePrefUtils by inject()

    override fun setViewBinding(): ActivityPermissionBinding {
        return ActivityPermissionBinding.inflate(layoutInflater)
    }

    override fun initView() {
        logEvent(EventName.permission_open)

        binding.tvContinue.text = getString(if (permissionUtils.isGrantMultiplePermissions(Constants.STORAGE_PERMISSION)) R.string.skip else R.string.tv_continue)
        binding.ivSwitch.setOnCheckedChangeListener { _, isChecked ->
            logEvent(EventName.permission_allow_click)
            binding.ivSwitch.isEnabled = !isChecked
        }

        binding.ivSwitch.tap {
            if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.STORAGE_PERMISSION)) {
                binding.ivSwitch.isChecked = false
                WarningPermissionDialogFragment().show(supportFragmentManager, javaClass.name)
            } else {
                callStoragePermission.launch(Constants.STORAGE_PERMISSION)
            }
        }
        binding.tvContinue.tap {
            logEvent(EventName.permission_continue_click)
            sharePref.isPassPermission = true
            if (sharePref.isPassInterest) {
                if (sharePref.isPassColorPaletteStart) {
                    launchActivity(ContainerActivity::class.java)
                    finishAffinity()
                } else {
                    launchActivity(ColorPaletteStartActivity::class.java)
                    finishAffinity()
                }
            } else {
                launchActivity(InterestActivity::class.java)
                finishAffinity()
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                binding.ivSwitch.isChecked = permissionUtils.isGrantMultiplePermissions(Constants.STORAGE_PERMISSION)
                if (binding.ivSwitch.isChecked) {
                    binding.ivSwitch.isEnabled = false
                }
            }
        }
    }

}