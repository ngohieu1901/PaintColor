package com.paintcolor.drawing.paint.presentations.feature.interest

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityInterestBinding
import com.paintcolor.drawing.paint.firebase.ads.RemoteName.NATIVE_INTEREST
import com.paintcolor.drawing.paint.firebase.ads.RemoteName.NATIVE_ON
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.feature.color_palette.start.ColorPaletteStartActivity
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InterestActivity : BaseActivity<ActivityInterestBinding>() {
    private val viewModel by viewModel<InterestViewModel>()
    private val sharePref: SharePrefUtils by inject()

    private lateinit var adapter: InterestAdapter
    override fun setViewBinding(): ActivityInterestBinding =
        ActivityInterestBinding.inflate(layoutInflater)

    override fun onResume() {
        super.onResume()
        logEvent(EventName.interest_view)
    }

    override fun initView() {

        viewModel.getAllItemInterest()

        adapter = InterestAdapter {
            viewModel.selectInterest(it)
        }

        binding.apply {
            rvInterest.adapter = adapter
            ivSelect.tap {
                logEvent(EventName.interest_next_click)
                sharePref.isPassInterest = true
                if (sharePref.isPassColorPaletteStart) {
                    launchActivity(ContainerActivity::class.java)
                    finishAffinity()
                } else {
                    launchActivity(ColorPaletteStartActivity::class.java)
                    finishAffinity()
                }
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collectLatest {
                    when (it) {
                        is InterestUiState.Loading -> {
                            showLoading()
                        }

                        is InterestUiState.Success -> {
                            dismissLoading()
                            adapter.submitList(it.listInterest)
                            if (it.listInterest.filter { interestModel -> interestModel.isSelected }.size > 1) {
                                binding.ivSelect.visible()
                            } else {
                                binding.ivSelect.gone()
                            }
                        }

                        is InterestUiState.Error -> {
                            dismissLoading()
                            toast(it.message)
                        }
                    }
                }
            }
        }
    }
}