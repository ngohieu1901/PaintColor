package com.paintcolor.drawing.paint.presentations.feature.drawing

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amazic.library.ads.admob.Admob
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentDrawingBinding
import com.paintcolor.drawing.paint.domain.model.CameraMode
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.components.dialogs.RewardDialog
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.CameraViewModel
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.SketchImageViewModel
import com.paintcolor.drawing.paint.presentations.feature.drawing.view_model.ToolsViewModel
import com.paintcolor.drawing.paint.utils.FilesUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.invisible
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DrawingFragment : BaseFragment<FragmentDrawingBinding>(FragmentDrawingBinding::inflate) {
    private val args by navArgs<DrawingFragmentArgs>()
    private val viewModel by activityViewModel<ToolsViewModel>()
    private val cameraViewModel by viewModel<CameraViewModel>()
    private val sketchImageViewModel by viewModel<SketchImageViewModel>()
    private val fileUtils : FilesUtils by inject()

    private lateinit var toolsDrawingAdapter: ToolsDrawingAdapter

    override fun initData() {

        viewModel.initTools()

        fileUtils.getImagePathByImageUrl(args.urlTemplate)?.let {
            sketchImageViewModel.loadOriginalBitmap(it, args.isRemoveBackground)
        } ?: {
            toast("Template is not exist!")
            popBackStack()
        }
        val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_EDIT)

        toolsDrawingAdapter = ToolsDrawingAdapter(
            isDisableReward = isDisableReward,
            onSelectTools = {
                if (it.nameResource == R.string.flip) {
                    sketchImageViewModel.flipHorizontal()
                    viewModel.selectTools(it)
                } else {
                    if (it.isViewedReward || isDisableReward) {
                        viewModel.selectTools(it)
                    } else {
                        viewModel.selectTools(it.copy(isViewedReward = true))
                    }
                }

                when (it.nameResource) {
                    R.string.flip -> {
                        requireContext().logEvent(EventName.sketch_draw_flip_click)
                    }

                    R.string.lock -> {
                        requireContext().logEvent(EventName.sketch_draw_lock_click)
                    }

                    R.string.opacity -> {
                        requireContext().logEvent(EventName.sketch_draw_opacity_click)
                    }

                    R.string.flash -> {
                        requireContext().logEvent(EventName.sketch_draw_flash_click)
                    }
                }
            }
        )
    }

    override fun setupView() {

        binding.apply {
            cameraView.setLifecycleOwner(viewLifecycleOwner)
            cameraView.addCameraListener(Listener())

            rvTools.adapter = toolsDrawingAdapter

            ivBack.tap {
                popBackStack(R.id.tutorialFragment, true)
            }

            ivHome.tap {
                popBackStack(R.id.mainFragment)
            }

            llVideo.tap {
                requireContext().logEvent(EventName.sketch_draw_record_click, Bundle().apply {
                    putString(ParamName.record_choose, "video")
                })
                cameraViewModel.setCameraMode(CameraMode.VIDEO)
            }

            llPhoto.tap {
                requireContext().logEvent(EventName.sketch_draw_record_click, Bundle().apply {
                    putString(ParamName.record_choose, "photo")
                })
                cameraViewModel.setCameraMode(CameraMode.PHOTO)
            }

            opacitySlider.setOnSeekBarChangeListener(object :
                android.widget.SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: android.widget.SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    sketchView.setAlpha(progress)
                }

                override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {

                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.sketch_draw_view)
    }

    override fun onStop() {
        super.onStop()
        if (binding.cameraView.isTakingVideo) {
            binding.cameraView.stopVideo()
        }
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    override fun dataCollect() {
        val isEnableRewardAll = Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ALL) && Admob.getInstance().checkCondition(context, RemoteName.REWARDED_EDIT)
        if (isEnableRewardAll) binding.llUnlockAll.visible() else binding.llUnlockAll.gone()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collectLatest {
                        toolsDrawingAdapter.submitList(it.listTools)

                        val isLockSelected =
                            it.listTools.any { tool -> tool.nameResource == R.string.lock && tool.isSelected }
                        val isRecordSelected =
                            it.listTools.any { tool -> tool.nameResource == R.string.record && tool.isSelected }
                        val isOpacitySelected =
                            it.listTools.any { tool -> tool.nameResource == R.string.opacity && tool.isSelected }
                        val isFlashSelected =
                            it.listTools.any { tool -> tool.nameResource == R.string.flash && tool.isSelected }

                        binding.apply {
                            llRecord.isVisible = isRecordSelected
                            llOpacity.isVisible = isOpacitySelected

                            sketchView.setSketchLocked(isLockSelected)

                            if (isFlashSelected) {
                                cameraView.flash = Flash.TORCH
                            } else {
                                cameraView.flash = Flash.OFF
                            }

                            if (
                                it.listTools.any { paintBrush -> !paintBrush.isViewedReward } &&
                                isEnableRewardAll
                            ) {
                                llUnlockAll.visible()
                                tvCurrentCountWatched.text = it.countWatchedVideoReward.toString()
                                when(it.countWatchedVideoReward) {
                                    0 -> {
                                        progressBar.progress = 0
                                    }
                                    1 -> {
                                        progressBar.progress = 50
                                    }
                                    2 -> {
                                        progressBar.progress = 100
                                    }
                                }
                            } else {
                                llUnlockAll.gone()
                            }
                        }
                    }
                }
                launch {
                    launch {
                        sketchImageViewModel.uiStateStore.collectLatest {
                            it.imageBitmap?.let { imageBitmap ->
                                binding.sketchView.setSketchBitmap(imageBitmap)
                            }
                        }
                    }
                    launch {
                        sketchImageViewModel.loadingState.collectLatest {
                            renderLoading(it)
                        }
                    }
                    launch {
                        sketchImageViewModel.errorsState.collectLatest {
                            renderError(it)
                        }
                    }
                    launch {
                        sketchImageViewModel.saveImageResult.collectLatest { result ->
                            result.fold(
                                onSuccess = { path ->
                                    Timber.d("pathImageSaved: $path")
                                    dismissLoading()
                                    toast(getString(R.string.save_image_successfully))
                                },
                                onFailure = {
                                    dismissLoading()
                                    toast(getString(R.string.failed_to_save_image))
                                }
                            )
                        }
                    }
                }
                launch {
                    cameraViewModel.uiStateStore.collectLatest {
                        binding.apply {
                            if (it.cameraMode == CameraMode.VIDEO) {
                                cameraView.mode = Mode.VIDEO
                                ivStateRecord.setImageResource(R.drawable.ic_take_picture)
                                vSelectVideo.visible()
                                tvVideo.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                                vSelectPhoto.invisible()
                                tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_8E8E93))
                                if (it.isRecording) {
                                    ivStateRecord.setImageResource(R.drawable.ic_recording)
                                    ivStateRecord.tap {
                                        if (binding.cameraView.isTakingVideo) {
                                            binding.cameraView.stopVideo()
                                        }
                                    }
                                    tvTime.visible()
                                    tvTime.text = String.format(
                                        "%02d:%02d:%02d",
                                        it.recordingTime / 3600,
                                        it.recordingTime % 3600 / 60,
                                        it.recordingTime % 60
                                    )
                                } else {
                                    ivStateRecord.setImageResource(R.drawable.ic_start_record)
                                    ivStateRecord.tap {
                                        binding.cameraView.takeVideoSnapshot(
                                            sketchImageViewModel.getOutputVideoFile()
                                        )
                                    }
                                    tvTime.gone()
                                    tvTime.text = "00:00:00"
                                }
                            } else {
                                tvTime.gone()
                                cameraView.mode = Mode.PICTURE
                                ivStateRecord.setImageResource(R.drawable.ic_take_picture)
                                ivStateRecord.tap {
                                    binding.cameraView.takePictureSnapshot()
                                }
                                vSelectVideo.invisible()
                                tvVideo.setTextColor(ContextCompat.getColor(requireContext(), R.color.color_8E8E93))
                                vSelectPhoto.visible()
                                tvPhoto.setTextColor(ContextCompat.getColor(requireContext(), com.amazic.mylibrary.R.color.black))
                            }
                        }
                    }
                }
            }
        }
    }

    private inner class Listener : CameraListener() {
        override fun onPictureTaken(result: com.otaliastudios.cameraview.PictureResult) {
            super.onPictureTaken(result)
            result.toBitmap {
                if (it != null) {
                    sketchImageViewModel.savePhotoCaptured(bitmap = it)
                }
            }
        }

        override fun onVideoRecordingStart() {
            super.onVideoRecordingStart()
            cameraViewModel.startRecording()
        }

        override fun onVideoRecordingEnd() {
            super.onVideoRecordingEnd()
            cameraViewModel.stopRecording()
        }

        override fun onVideoTaken(result: VideoResult) {
            super.onVideoTaken(result)
            toast(getString(R.string.video_saved))
        }
    }
}