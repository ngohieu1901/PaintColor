package com.paintcolor.drawing.paint.presentations.feature.view_color

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amazic.library.ads.admob.Admob
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.databinding.FragmentViewImageColoringBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.DeletePaletteDialog
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningPermissionDialogFragment
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationViewModel
import com.paintcolor.drawing.paint.widget.callMultiplePermissions
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.loadImageFromFile
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ViewImageColoringFragment :
    BaseFragment<FragmentViewImageColoringBinding>(FragmentViewImageColoringBinding::inflate) {
    private val args: ViewImageColoringFragmentArgs by navArgs()
    private val myCreationViewModel: MyCreationViewModel by activityViewModel()
    private val viewModel: FilesViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()
    private val callStoragePermission = callMultiplePermissions {}

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.paint_result_view)
    }

    override fun setupView() {

        val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_DOWNLOAD)

        binding.apply {
            myCreationViewModel.getColoringMyCreation(args.coloringMyCreation.imagePath)?.let {
                if (it.isViewedRewardShare || isDisableReward) ivRewardShare.gone()
                if (it.isViewedRewardDownload || isDisableReward) ivRewardDownload.gone()
            } ?: {
                if (args.coloringMyCreation.isViewedRewardShare || isDisableReward) ivRewardShare.gone()
                if (args.coloringMyCreation.isViewedRewardDownload || isDisableReward) ivRewardDownload.gone()
            }

            binding.ivImage.loadImageFromFile(args.coloringMyCreation.imagePath)
            tvName.text = File(args.coloringMyCreation.imagePath).name

            ivBack.tap {
                popBackStack()
            }
            ivEdit.tap {
                requireContext().logEvent(EventName.paint_result_edit_click)
                safeNavigate(
                    ViewImageColoringFragmentDirections.actionViewImageColoringFragmentToNavColoring(
                        urlTemplate = args.coloringMyCreation.imagePath
                    )
                )
            }
            ivShare.tap {
                requireContext().logEvent(EventName.paint_result_share_click)
                shareViewModel.shareImage(args.coloringMyCreation.imagePath)
                myCreationViewModel.updateStateItemColoring(args.coloringMyCreation.copy(isViewedRewardShare = true))
            }
            ivDownload.tap {
                requireContext().logEvent(EventName.paint_result_download_click)
                if (permissionUtils.isGrantMultiplePermissions(Constants.STORAGE_PERMISSION)){
                    viewModel.downloadImage(args.coloringMyCreation.imagePath)
                    myCreationViewModel.updateStateItemColoring(args.coloringMyCreation.copy(isViewedRewardDownload = true))
                } else {
                    if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.STORAGE_PERMISSION)) {
                        WarningPermissionDialogFragment().show(childFragmentManager, javaClass.name)
                    } else {
                        callStoragePermission.launch(Constants.STORAGE_PERMISSION)
                    }
                }
            }
            ivDelete.tap {
                requireContext().logEvent(EventName.paint_result_delete_click)
                DeletePaletteDialog(onDelete = {
                    viewModel.deleteFile(args.coloringMyCreation.imagePath)
                }).show(childFragmentManager,"DeletePaletteDialog")
            }
            llBackToHome.tap {
                popBackStack(R.id.mainFragment)
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadingState.collectLatest {
                        renderLoading(it)
                    }
                }
                launch {
                    viewModel.downloadResult.collectLatest {
                        if (it) {
                            toast(getString(R.string.download_image_successfully))
                        } else {
                            toast(getString(R.string.failed_to_download_image))
                        }
                    }
                }
                launch {
                    shareViewModel.shareIntentResult.distinctUntilChanged().collectLatest { intent ->
                        if (intent == null) {
                            toast(getString(R.string.failed_to_share_image))
                        } else {
                            AdsHelper.disableResume(requireActivity())
                            startActivity(intent)
                        }
                    }
                }
                launch {
                    viewModel.deleteResult.collectLatest {
                        if (it) {
                            toast(getString(R.string.delete_image_successfully))
                            popBackStack()
                        } else {
                            toast(getString(R.string.failed_to_delete_image))
                        }
                    }
                }
            }
        }
    }
}