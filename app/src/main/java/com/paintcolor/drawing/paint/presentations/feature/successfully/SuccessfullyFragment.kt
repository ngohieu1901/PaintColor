package com.paintcolor.drawing.paint.presentations.feature.successfully

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amazic.library.ads.admob.Admob
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.databinding.FragmentSuccessfullyBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningPermissionDialogFragment
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.widget.callMultiplePermissions
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.loadImageFromFile
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SuccessfullyFragment: BaseFragment<FragmentSuccessfullyBinding>(FragmentSuccessfullyBinding::inflate) {
    private val args: SuccessfullyFragmentArgs by navArgs()
    private val viewModel by viewModel<FilesViewModel>()
    private val shareViewModel: ShareViewModel by viewModel()
    private val callStoragePermission = callMultiplePermissions {}
    private var isViewedRewardShare = false
    private var isViewedRewardDownload = false

    override fun initData() {
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.success_view)
    }

    override fun setupView() {

        val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_DOWNLOAD)

        binding.apply {
            ivPreview.loadImageFromFile(args.imagePath)
            if (isDisableReward) {
                ivRewardShare.gone()
                ivRewardDownload.gone()
            }
            llExplore.tap {
                requireContext().logEvent(EventName.explore_more_click)
                popBackStack(R.id.mainFragment)
            }
            llDownload.tap {
                requireContext().logEvent(EventName.success_download_click)
                if (permissionUtils.isGrantMultiplePermissions(Constants.STORAGE_PERMISSION)){
                    viewModel.downloadImage(args.imagePath)
                    if (isViewedRewardDownload || isDisableReward) {
                        viewModel.downloadImage(args.imagePath)
                    } else {
                        viewModel.downloadImage(args.imagePath)
                        isViewedRewardDownload = true
                        binding.ivRewardDownload.gone()
                    }
                } else {
                    if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.STORAGE_PERMISSION)) {
                        WarningPermissionDialogFragment().show(childFragmentManager, javaClass.name)
                    } else {
                        callStoragePermission.launch(Constants.STORAGE_PERMISSION)
                    }
                }
            }
            llShare.tap {
                requireContext().logEvent(EventName.success_share_click)
                if (isViewedRewardShare || isDisableReward) {
                    shareViewModel.shareImage(args.imagePath)
                } else {
                    shareViewModel.shareImage(args.imagePath)
                    isViewedRewardShare = true
                    binding.ivRewardShare.gone()
                }
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
            }
        }
    }
}