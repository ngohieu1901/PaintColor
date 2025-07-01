package com.paintcolor.drawing.paint.presentations.feature.coloring

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.transition.MaterialFade
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentColoringBinding
import com.paintcolor.drawing.paint.event_bus.DownloadCompleteEvent
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.components.bottom_sheet.ChooseActionBottomSheet
import com.paintcolor.drawing.paint.presentations.feature.main.MainFragmentDirections
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import timber.log.Timber

class ColoringFragment: BaseFragment<FragmentColoringBinding>(FragmentColoringBinding::inflate) {
    private val viewModel: ColoringViewModel by activityViewModel()
    private lateinit var templateCategoryAdapter: TemplateCategoryAdapter

    override fun initData() {
        enterTransition = MaterialFade().apply {
            duration = 300L
        }
        returnTransition = MaterialFade().apply {
            duration = 300L
        }

        templateCategoryAdapter = TemplateCategoryAdapter(
            onOpenCategory = { categoryName ->
                requireContext().logEvent(EventName.home_see_all_click, bundle = Bundle().apply {
                    putString(ParamName.category_name, categoryName)
                })
                safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToDetailTemplateCategoryFragment(categoryName))
            },
            onSelect = {
                ChooseActionBottomSheet(
                    onSelectColoring = {
                        safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToNavColoring(urlTemplate = it.imagePath))
                    },
                    onSelectDrawing = {
                        safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToNavDrawing(urlTemplate = it.imagePath))
                    }
                ).show(childFragmentManager, "ChooseActionBottomSheet")
            },
            onDownload = {
                viewModel.downloadTemplate(it.imagePath)
            }
        )
    }

    override fun setupView() {
        binding.apply {
            rvData.adapter = templateCategoryAdapter
            rvData.itemAnimator = null
            llCreate.tap {
                requireContext().logEvent(EventName.home_create_click)
                safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToNavColoring(""))
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collectLatest {
                        Timber.d("${it.listTemplateInCategory}")
                        templateCategoryAdapter.submitList(it.listTemplateCategory)
                    }
                }
                launch {
                    viewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
                launch {
                    viewModel.loadingState.collectLatest {
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
        viewModel.handleDownloadComplete(event.downloadId)
    }
}