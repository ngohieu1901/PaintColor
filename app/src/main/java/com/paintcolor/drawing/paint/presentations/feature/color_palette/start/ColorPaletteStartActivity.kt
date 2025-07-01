package com.paintcolor.drawing.paint.presentations.feature.color_palette.start

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.paintcolor.drawing.paint.base.BaseActivity
import com.paintcolor.drawing.paint.databinding.ActivityColorPaletteStartBinding
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.feature.color_palette.ColorPalettesAdapter
import com.paintcolor.drawing.paint.presentations.feature.container.ContainerActivity
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.launchActivity
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class ColorPaletteStartActivity : BaseActivity<ActivityColorPaletteStartBinding>() {
    private val viewModel by viewModel<ColorPaletteStartViewModel>()
    private val sharePref: SharePrefUtils by inject()

    private lateinit var adapter: ColorPalettesAdapter

    override fun setViewBinding(): ActivityColorPaletteStartBinding = ActivityColorPaletteStartBinding.inflate(layoutInflater)

    override fun initView() {
        logEvent(EventName.color_palette_view)

        viewModel.collectColorPalettesPlain()

        var idPaletteStartSelected = -1L

        adapter = ColorPalettesAdapter(isShowDotMore = false, onSelectPalette = { colorPalette, pos ->
            logEvent(EventName.color_palette_color_click, bundle = Bundle().apply {
                putString(ParamName.color_type, getString(colorPalette.nameResource))
            })
            idPaletteStartSelected = colorPalette.id
            viewModel.setSelectedPalette(colorPalette)
            binding.rvPalette.scrollToPosition(pos)
        })

        binding.rvPalette.adapter = adapter
        binding.ivSelect.tap {
            sharePref.isPassColorPaletteStart = true
            sharePref.idPaletteStartSelected = idPaletteStartSelected
            launchActivity(ContainerActivity::class.java)
            finishAffinity()
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiStateStore.collect {
                        adapter.submitList(it.listColorPalette)
                        if (it.listColorPalette.any { colorPalette -> colorPalette.isSelected }) {
                            binding.ivSelect.visible()
                        } else {
                            binding.ivSelect.gone()
                        }
                    }
                }
                launch {
                    viewModel.loadingState.collectLatest {
                        renderLoading(it)
                    }
                }
                launch {
                    viewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
            }
        }
    }

}