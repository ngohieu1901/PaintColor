package com.paintcolor.drawing.paint.presentations.feature.tutorial

import android.os.Bundle
import androidx.navigation.fragment.navArgs
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.databinding.FragmentTutorialBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.components.dialogs.RemoveBackgroundDialog
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningPermissionDialogFragment
import com.paintcolor.drawing.paint.widget.callMultiplePermissions
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap

class TutorialFragment : BaseFragment<FragmentTutorialBinding>(FragmentTutorialBinding::inflate) {
    private lateinit var removeBackgroundDialog: RemoveBackgroundDialog
    private val args: TutorialFragmentArgs by navArgs()
    private val callCameraAndMicroPermission = callMultiplePermissions {}

    override fun initData() {
        removeBackgroundDialog = RemoveBackgroundDialog(
            onRemove = {
                requireContext().logEvent(EventName.sketch_continue_click, bundle = Bundle().apply {
                    putString(ParamName.choose, "remove")
                })
                safeNavigate(TutorialFragmentDirections.actionTutorialFragmentToDrawingFragment(urlTemplate = args.urlTemplate, isRemoveBackground = true))
            },
            onNotRemove = {
                requireContext().logEvent(EventName.sketch_continue_click, bundle = Bundle().apply {
                    putString(ParamName.choose, "no thanks")
                })
                safeNavigate(TutorialFragmentDirections.actionTutorialFragmentToDrawingFragment(urlTemplate = args.urlTemplate, isRemoveBackground = false))
            }
        )
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.sketch_tutorial_view)
    }

    override fun setupView() {
        binding.ivBack.tap {
            popBackStack()
        }
        binding.tvContinue.tap {
            if (permissionUtils.isGrantMultiplePermissions(Constants.CAMERA_AND_MICROPHONE_PERMISSION)) {
                removeBackgroundDialog.show(childFragmentManager, removeBackgroundDialog.tag)
            } else {
                if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.CAMERA_AND_MICROPHONE_PERMISSION)) {
                    WarningPermissionDialogFragment().show(childFragmentManager, javaClass.name)
                } else {
                    callCameraAndMicroPermission.launch(Constants.CAMERA_AND_MICROPHONE_PERMISSION)
                }
            }
        }
    }

    override fun dataCollect() {

    }
}