package com.paintcolor.drawing.paint.presentations.feature.detail_template_category

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amazic.library.ads.admob.Admob
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentDetailCategoryBinding
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.event_bus.DownloadCompleteEvent
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.components.bottom_sheet.ChooseActionBottomSheet
import com.paintcolor.drawing.paint.presentations.feature.coloring.ColoringViewModel
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber

class DetailTemplateCategoryFragment: BaseFragment<FragmentDetailCategoryBinding>(FragmentDetailCategoryBinding::inflate) {
    private val coloringViewModel by activityViewModel<ColoringViewModel>()
    private val args: DetailTemplateCategoryFragmentArgs by navArgs()
    private lateinit var template: DetailTemplateCategoryAdapter

    override fun initData() {
        coloringViewModel.getAllTemplateInCategory(args.nameCategory)

        val isDisableReward = !Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ITEMS)
        template = DetailTemplateCategoryAdapter(
            isDisableReward = isDisableReward,
            onSelectTemplate = {
                if (it.isViewedReward || isDisableReward) {
                    chooseType(it)
                } else {
                    chooseType(it)
                    coloringViewModel.updateStateRewardTemplate(it)
                }
            },
            onDownloadTemplate = {
                coloringViewModel.downloadTemplate(it.imagePath)
            }
        )
    }

    private fun chooseType(it: Template) {
        ChooseActionBottomSheet(
            onSelectColoring = {
                requireContext().logEvent(EventName.category_item_choose, bundle = Bundle().apply {
                    putString(ParamName.category_item_name, it.imagePath.substringAfterLast("/"))
                    putString(ParamName.choose_type, "Coloring")
                })
                safeNavigate(DetailTemplateCategoryFragmentDirections.actionDetailTemplateCategoryToNavColoring(urlTemplate = it.imagePath))
            },
            onSelectDrawing = {
                requireContext().logEvent(EventName.category_item_choose, bundle = Bundle().apply {
                    putString(ParamName.category_item_name, it.imagePath.substringAfterLast("/"))
                    putString(ParamName.choose_type, "Sketch")
                })
                safeNavigate(DetailTemplateCategoryFragmentDirections.actionDetailTemplateCategoryFragmentToNavDrawing(urlTemplate = it.imagePath))
            }
        ).show(childFragmentManager, "ChooseActionBottomSheet")
    }

    override fun setupView() {
        binding.apply {
            ivBack.tap {
                popBackStack()
            }
            tvHeader.text = args.nameCategory
            rvTrending.adapter = template
            rvTrending.itemAnimator = null

            llUnlockAll.tap {
                coloringViewModel.turnOffAllRewardAdsInCategory(args.nameCategory)
            }
        }
    }

    override fun dataCollect() {
        val isEnableRewardAll = Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ALL) && Admob.getInstance().checkCondition(context, RemoteName.REWARDED_ITEMS)
        if (isEnableRewardAll) binding.llUnlockAll.visible() else binding.llUnlockAll.gone()
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    coloringViewModel.uiStateStore.collectLatest {
                        Timber.e("listTemplateInCategoryXXX: ${it.listTemplateInCategory}")
                        template.submitList(it.listTemplateInCategory)

                        if (
                            it.listTemplateInCategory.any { paintBrush -> !paintBrush.isViewedReward } &&
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
                    coloringViewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
                launch {
                    coloringViewModel.loadingState.collectLatest {
                        renderLoading(it)
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDownloadComplete(event: DownloadCompleteEvent) {
        coloringViewModel.handleDownloadComplete(event.downloadId)
    }
}