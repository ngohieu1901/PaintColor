package com.paintcolor.drawing.paint.presentations.feature.my_creation.sketch

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.constants.Constants
import com.paintcolor.drawing.paint.databinding.FragmentMyCreationSketchBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.DeleteDialog
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningPermissionDialogFragment
import com.paintcolor.drawing.paint.presentations.components.popup_window.MoreActionMyCreationPopup
import com.paintcolor.drawing.paint.presentations.components.views.BottomNavView.Companion.COLORING_FRAGMENT
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.presentations.feature.main.MainFragmentDirections
import com.paintcolor.drawing.paint.presentations.feature.main.MainViewModel
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationViewModel
import com.paintcolor.drawing.paint.widget.callMultiplePermissions
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyCreationSketchFragment :
    BaseFragment<FragmentMyCreationSketchBinding>(FragmentMyCreationSketchBinding::inflate) {
    private val viewModel: MyCreationViewModel by activityViewModel()
    private val mainViewModel by activityViewModel<MainViewModel>()
    private val fileViewModel: FilesViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()
    private val callStoragePermission = callMultiplePermissions {}

    private lateinit var sketchAdapter: MyCreationSketchAdapter

    override fun initData() {
        sketchAdapter = MyCreationSketchAdapter(
            onOpenItem = {
                if (it.isVideo) {
                    safeNavigateParentNav(
                        MainFragmentDirections.actionMainFragmentToViewVideoDrawingFragment(it)
                    )
                } else {
                    safeNavigateParentNav(
                        MainFragmentDirections.actionMainFragmentToViewImageDrawingFragment(it)
                    )
                }
            },
            onClickMoreOption = { data, view ->
                val popupWindow = MoreActionMyCreationPopup(
                    requireContext(),
                    isNotUseEdit = true,
                    isViewedRewardShare = data.isViewedRewardShare,
                    isViewedRewardDownload = data.isViewedRewardDownload,
                    onDownload = {
                        if (permissionUtils.isGrantMultiplePermissions(Constants.STORAGE_PERMISSION)){
                            fileViewModel.downloadImage(data.filePath)
                            viewModel.updateStateItemSketch(data.copy(isViewedRewardDownload = true))
                        } else {
                            if (permissionUtils.canShowAllListPermissionDialogSystem(Constants.STORAGE_PERMISSION)) {
                                WarningPermissionDialogFragment().show(childFragmentManager, javaClass.name)
                            } else {
                                callStoragePermission.launch(Constants.STORAGE_PERMISSION)
                            }
                        }
                    },
                    onShare = {
                        shareViewModel.shareImage(data.filePath)
                        viewModel.updateStateItemSketch(data.copy(isViewedRewardShare = true))
                    },
                    onDelete = {
                        DeleteDialog{
                            fileViewModel.deleteFile(data.filePath)
                        }.show(childFragmentManager, "DeleteDialog")
                    })
                showPopupWindow(view, popupWindow)
            }
        )
    }

    override fun setupView() {
        binding.apply {
            rvDrawing.adapter = sketchAdapter
            llExplore.tap {
                requireContext().logEvent(EventName.my_creation_explore_click)
                mainViewModel.setSelectedFragment(COLORING_FRAGMENT)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.my_creation_drawing_view)
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collectLatest {
                        sketchAdapter.submitList(it.listSketch)

                        if (it.listSketch.isEmpty()) {
                            binding.cvDrawing.gone()
                            binding.llNoData.visible()
                        } else {
                            binding.cvDrawing.visible()
                            binding.llNoData.gone()
                        }
                    }
                }
                launch {
                    fileViewModel.loadingState.collectLatest {
                        renderLoading(it)
                    }
                }
                launch {
                    fileViewModel.downloadResult.collectLatest {
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
                    fileViewModel.deleteResult.collectLatest {
                        if (it) {
                            viewModel.getAllFilesColoringAndDrawing()
                            toast(getString(R.string.delete_image_successfully))
                        } else {
                            toast(getString(R.string.failed_to_delete_image))
                        }
                    }
                }
            }
        }
    }
}