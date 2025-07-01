package com.paintcolor.drawing.paint.presentations.feature.paint_brush

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.amazic.library.ads.admob.Admob
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentPaintBrushBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PaintBrushFragment :
    BaseFragment<FragmentPaintBrushBinding>(FragmentPaintBrushBinding::inflate) {
    private val viewModel: PaintBrushViewModel by activityViewModel()
    private lateinit var paintBrushGridAdapter: PaintBrushGridAdapter

    override fun initData() {
        val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_EDIT)

        paintBrushGridAdapter = PaintBrushGridAdapter(isDisableReward = isDisableReward) {
            requireContext().logEvent(EventName.edit_paint_pen_click, bundle = Bundle().apply {
                putString(ParamName.paint_pen, it.styleTools.toString())
            })
            if (it.isViewedReward || isDisableReward) {
                viewModel.selectPaintBrush(it)
            } else {
                viewModel.selectPaintBrush(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.paint_brush_view)
    }

    override fun setupView() {
        binding.apply {
            ivBack.tap {
                popBackStack()
            }
            llUnlockAll.tap {
                viewModel.turnOffAllRewardAds()
            }
            rvTrending.adapter = paintBrushGridAdapter
        }
    }

    override fun dataCollect() {
        val isEnableRewardAll = Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ALL) && Admob.getInstance().checkCondition(context, RemoteName.REWARDED_EDIT)
        if (isEnableRewardAll) binding.llUnlockAll.visible() else binding.llUnlockAll.gone()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collectLatest {
                        paintBrushGridAdapter.submitList(it.listAllPaintBrush)
                        if (
                            it.listAllPaintBrush.any { paintBrush -> !paintBrush.isViewedReward } &&
                            isEnableRewardAll
                        ) {
                            binding.llUnlockAll.visible()
                            binding.tvCurrentCountWatched.text = it.countWatchedVideoReward.toString()
                            when(it.countWatchedVideoReward) {
                                0 -> {
                                    binding.progressBar.progress = 0
                                }
                                1 -> {
                                    binding.progressBar.progress = 50
                                }
                                2 -> {
                                    binding.progressBar.progress = 100
                                }
                            }
                        } else {
                            binding.llUnlockAll.gone()
                        }
                    }
                }

                launch {
                    viewModel.errorsState.collectLatest {
                        toast(it.message.toString())
                    }
                }
            }
        }
    }
}