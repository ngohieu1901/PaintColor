package com.paintcolor.drawing.paint.presentations.feature.custom_your_palette

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentCustomYourPaletteBinding
import com.paintcolor.drawing.paint.domain.model.ColorHex
import com.paintcolor.drawing.paint.domain.model.ColorPalette
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.views.color_picker.ColorObserver
import com.paintcolor.drawing.paint.widget.getTextEx
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toColorHex
import com.paintcolor.drawing.paint.widget.toast
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class CustomYourPaletteFragment : BaseFragment<FragmentCustomYourPaletteBinding>(FragmentCustomYourPaletteBinding::inflate), ColorObserver {
    private val viewModel by viewModel<CustomYourPaletteViewModel>()
    private val args: CustomYourPaletteFragmentArgs by navArgs()
    private lateinit var colorCustomAdapter: ColorCustomAdapter

    private val defaultColorCode by lazy { ColorHex("#44ff26") }

    override fun initData() {
        viewModel.insertListColor(args.colorPalette)

        colorCustomAdapter = ColorCustomAdapter(
            onSelectColor = {
                viewModel.selectColor(it)
            },
            onAddColor = {
                viewModel.addColor(it.copy(colorCode = binding.colorView.color.toColorHex()))
            }
        )
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.custom_palettes_view)
    }

    override fun setupView() {
        binding.apply {
            args.colorPalette?.let {
                edName.setText(it.nameString)
            }
            rvColorCustom.adapter = colorCustomAdapter
            rvColorCustom.itemAnimator = null

            colorView.subscribe(this@CustomYourPaletteFragment)
            colorView.setBrightness(1f)
            colorView.setColor(defaultColorCode.toColorInt(), true)

            ivDemo.setBackgroundColor(defaultColorCode.toColorInt())

            isbAlpha.setProgress(100f)
            isbAlpha.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams?) {

                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar?) {
                    if (seekBar != null) {
                        colorView.setBrightness(seekBar.progress / 100f)
                    }
                }
            }

            ivSave.tap {
                requireContext().logEvent(EventName.custom_palettes_save_click)
                when {
                    edName.getTextEx().isBlank() -> {
                        toast(getString(R.string.please_enter_your_name_palette))
                    }
                    viewModel.currentState.listColor.all { it.colorCode.isBlank() } -> {
                        toast(getString(R.string.please_select_at_least_1_color))
                    }
                    else -> {
                        val listColorCode = viewModel.currentState.listColor.map {
                            it.colorCode
                        }
                        if (args.colorPalette != null) {
                            val colorPalette = args.colorPalette
                            viewModel.updateColorPalette(colorPalette!!.copy(listColor = listColorCode, nameString = edName.getTextEx()))
                        } else {
                            val colorPalette = ColorPalette(
                                nameString = edName.getTextEx(),
                                listColor = listColorCode,
                                isCustomPalette = true
                            )
                            viewModel.insertColorPaletteToDb(colorPalette)
                        }

                        popBackStack()
                    }
                }
            }

            ivBack.tap {
                popBackStack()
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiStateStore.collectLatest {
                    Timber.d("listColor: $it")
                    colorCustomAdapter.submitList(it.listColor)
                }
            }
        }
    }

    override fun onColor(color: Int, fromUser: Boolean, shouldPropagate: Boolean) {
        binding.ivDemo.setBackgroundColor(color)
        viewModel.updateColorSelected(colorCode = color.toColorHex())
    }
}