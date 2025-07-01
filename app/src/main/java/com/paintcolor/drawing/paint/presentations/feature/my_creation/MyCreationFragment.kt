package com.paintcolor.drawing.paint.presentations.feature.my_creation

import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amazic.library.ads.admob.Admob
import com.google.android.material.transition.MaterialFade
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentMyCreationBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationUiState.Companion.COLORING
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationUiState.Companion.SKETCH
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.invisible
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class MyCreationFragment :
    BaseFragment<FragmentMyCreationBinding>(FragmentMyCreationBinding::inflate) {
    private val viewModel: MyCreationViewModel by activityViewModel()

    override fun initData() {
        enterTransition = MaterialFade().apply {
            duration = 300L
        }
        returnTransition = MaterialFade().apply {
            duration = 300L
        }

        viewModel.getAllFilesColoringAndDrawing()
        viewModel.setFragmentSelected(COLORING)
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.my_creation_drawing_view)
    }

    override fun setupView() {
        binding.vpContainer.adapter = MyCreationContainerAdapter(requireActivity())
        binding.vpContainer.isUserInputEnabled = false

        binding.apply {
            llColoring.tap {
                viewModel.setFragmentSelected(COLORING)
            }
            llSketch.tap {
                viewModel.setFragmentSelected(SKETCH)
            }
            llUnlockAll.tap {
                viewModel.turnOffAllRewardAds()
            }
        }
    }

    override fun dataCollect() {
        val isEnableRewardAll =
            Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ALL) &&
            Admob.getInstance().checkCondition(context, RemoteName.REWARDED_DOWNLOAD)

        if (isEnableRewardAll) binding.llUnlockAll.visible() else binding.llUnlockAll.gone()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collectLatest {
                    binding.apply {
                        if (it.fragmentSelected == COLORING) {
                            vpContainer.currentItem = 0
                            vSelectColoring.visible()
                            vSelectSketch.invisible()
                            tvColoring.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            tvColoring.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                            tvSketch.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_not_select))
                            tvSketch.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                        } else {
                            vpContainer.currentItem = 1
                            vSelectColoring.invisible()
                            vSelectSketch.visible()
                            tvColoring.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_not_select))
                            tvColoring.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_medium)
                            tvSketch.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                            tvSketch.typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_semibold)
                        }

                        if (
                            (it.listSketch.any { paintBrush -> !paintBrush.isViewedRewardShare || !paintBrush.isViewedRewardDownload } ||
                                it.listColoring.any { paintBrush -> !paintBrush.isViewedRewardShare || !paintBrush.isViewedRewardDownload })
                            && isEnableRewardAll
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
        }
    }
}