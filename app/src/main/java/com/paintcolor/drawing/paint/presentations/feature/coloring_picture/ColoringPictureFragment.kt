package com.paintcolor.drawing.paint.presentations.feature.coloring_picture

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.ViewTreeObserver
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.amazic.library.ads.admob.Admob
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.coloring_source.StyleTools
import com.paintcolor.drawing.paint.constants.Constants.FolderName.COLORING_DATA
import com.paintcolor.drawing.paint.databinding.FragmentColoringPictureBinding
import com.paintcolor.drawing.paint.domain.model.RecentColor
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.ads.RemoteName
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.firebase.event.ParamName
import com.paintcolor.drawing.paint.presentations.components.dialogs.ClearDrawingDialog
import com.paintcolor.drawing.paint.presentations.components.dialogs.WarningImportDialog
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesUiState
import com.paintcolor.drawing.paint.presentations.feature.color_palette.select.ColorPalettesViewModel
import com.paintcolor.drawing.paint.presentations.feature.paint_brush.PaintBrushUiState
import com.paintcolor.drawing.paint.presentations.feature.paint_brush.PaintBrushViewModel
import com.paintcolor.drawing.paint.utils.FilesUtils
import com.paintcolor.drawing.paint.widget.invisible
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.resize
import com.paintcolor.drawing.paint.widget.setDisabledState
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toColorInt
import com.paintcolor.drawing.paint.widget.toast
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.navigation.koinNavGraphViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ColoringPictureFragment :
    BaseFragment<FragmentColoringPictureBinding>(FragmentColoringPictureBinding::inflate) {

    private val coloringViewModel: ColoringPictureViewModel by viewModel()
    private val paintBrushViewModel: PaintBrushViewModel by activityViewModel()
    private val colorPalettesViewModel: ColorPalettesViewModel by koinNavGraphViewModel(R.id.nav_coloring)
    private val args: ColoringPictureFragmentArgs by navArgs()

    private lateinit var paintBrushDrawingAdapter: PaintBrushDrawingAdapter
    private lateinit var colorDrawingAdapter: ColorDrawingAdapter
    private lateinit var colorSmallAdapter: ColorSmallAdapter

    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private val fileUtils: FilesUtils by inject()
    private var width = 0
    private var height = 0

    private var originalBitmap: Bitmap? = null

    private val warningImportDialog by lazy {
        WarningImportDialog {
            binding.drawView.visible()
            pickImageLauncher.launch(
                Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
            )
        }
    }

    override fun initData() {
        colorPalettesViewModel.collectAllColorPalettes()

        paintBrushDrawingAdapter = PaintBrushDrawingAdapter(
            isDisableReward = !Admob.getInstance().checkCondition(requireContext(), RemoteName.REWARDED_EDIT)
        ) { paintBrush, pos ->
            requireContext().logEvent(EventName.edit_paint_pen_click, bundle = Bundle().apply {
                putString(ParamName.paint_pen, paintBrush.styleTools.toString())
            })
            paintBrushViewModel.selectPaintBrush(paintBrush)
            binding.rvPen.scrollToPosition(pos)
        }

        colorDrawingAdapter = ColorDrawingAdapter { color ->
            colorPalettesViewModel.selectColor(singleColor = color)
            colorPalettesViewModel.insertRecentColorToDb(RecentColor(colorCode = color.colorCode))
            requireContext().logEvent(EventName.edit_paint_color_click)
        }

        colorSmallAdapter = ColorSmallAdapter()
    }

    override fun setupView() {
        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        val bitmapUserPick = fileUtils.getBitmapFromUri(uri) ?: return@let
                        originalBitmap = if (width > 0 && height > 0) bitmapUserPick.resize(
                            width,
                            height
                        ) else bitmapUserPick

                        binding.drawView.clearAllStack()
                        binding.drawView.setBitmap(originalBitmap)
                        initLogicUndoRedo()

                        binding.ivClear.tap {
                            requireContext().logEvent(EventName.edit_paint_clear_click)
                            ClearDrawingDialog {
                                binding.drawView.clearCanvas()
                                binding.drawView.setBitmap(originalBitmap)
                                disableAllView()
                            }.show(childFragmentManager, "ClearDrawingDialog1")
                        }

                        coloringViewModel.processIntent(
                            ColoringPictureUiIntent.MarkCanvasInitialized
                        )
                    }
                }
            }

        binding.apply {
            rvPen.adapter = paintBrushDrawingAdapter
            rvColor.adapter = colorDrawingAdapter
            rvSmallColor.adapter = colorSmallAdapter
            val layoutManager = FlexboxLayoutManager(context).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.CENTER
            }
            rvSmallColor.layoutManager = layoutManager

            if (args.urlTemplate.isBlank()) {
                ivImport.visible()
            }

            ivImport.tap {
                requireContext().logEvent(EventName.edit_paint_import_image_click)
                if (binding.drawView.canUndo() || binding.drawView.canRedo()) {
                    warningImportDialog.show(childFragmentManager, warningImportDialog.tag)
                } else {
                    AdsHelper.disableResume(requireActivity())
                    pickImageLauncher.launch(
                        Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        )
                    )
                }
            }

            llMore.tap {
                requireContext().logEvent(EventName.edit_paint_morepaint_click)
                safeNavigate(ColoringPictureFragmentDirections.actionColoringPictureFragmentToPaintBrushFragment())
            }

            llPaletteSelected.tap {
                safeNavigate(ColoringPictureFragmentDirections.actionColoringPictureFragmentToColorPalettesFragment())
            }

            ivNext.tap {
                colorPalettesViewModel.nextColorPalette()
            }

            ivPrevious.tap {
                colorPalettesViewModel.previousColorPalette()
            }

            tvSave.tap {
                requireContext().logEvent(EventName.edit_paint_save_click)

                coloringViewModel.processIntent(
                    ColoringPictureUiIntent.SaveImage(
                        bitmap = binding.drawView.bitmap,
                        oldFilePath =
                        if (args.urlTemplate.contains(COLORING_DATA))
                            args.urlTemplate
                        else
                            null
                    )
                )
            }
        }
        initDrawView()
        initTemplateBitmap()
        initLogicUndoRedo()
        initPaintBrush()
    }

    override fun onResume() {
        super.onResume()
        requireContext().logEvent(EventName.edit_paint_view)
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    combine(
                        paintBrushViewModel.paintBrushUiStateFlow,
                        colorPalettesViewModel.colorPalettesUiStateFlow
                    ) { paintBrushState, colorPalettesUiState ->
                        Pair(paintBrushState, colorPalettesUiState)
                    }.collectLatest {
                        renderStateView(it.first, it.second)
                    }
                }
                launch {
                    coloringViewModel.saveImageResult.collect { result ->
                        result.fold(
                            onSuccess = { path ->
                                Timber.d("pathImageSaved: $path")
                                dismissLoading()
                                toast(getString(R.string.save_image_successfully))
                                safeNavigate(
                                    ColoringPictureFragmentDirections.actionColoringPictureFragmentToSuccessfullyFragment(
                                        imagePath = path
                                    )
                                )
                            },
                            onFailure = {
                                dismissLoading()
                                toast(getString(R.string.failed_to_save_image))
                            }
                        )
                    }
                }
                launch {
                    coloringViewModel.loadingState.collectLatest {
                        renderLoading(it)
                    }
                }
                launch {
                    coloringViewModel.errorsState.collectLatest {
                        renderError(it)
                    }
                }
            }
        }
    }

    private fun renderStateView(
        paintBrushUiState: PaintBrushUiState,
        colorPalettesUiState: ColorPalettesUiState,
    ) {
        paintBrushDrawingAdapter.submitList(paintBrushUiState.listPaintBrushInColoringPicture)
        colorDrawingAdapter.submitList(colorPalettesUiState.listColorSelected)
        colorSmallAdapter.submitList(colorPalettesUiState.listColorSelected.take(5))

        binding.apply {
            seekBar.setProgress(paintBrushUiState.sizeBrushSelected)

            if (colorPalettesUiState.colorPaletteSelected.isCustomPalette) {
                tvNamePaletteSelected.text = colorPalettesUiState.colorPaletteSelected.nameString
            } else {
                tvNamePaletteSelected.text =
                    getString(colorPalettesUiState.colorPaletteSelected.nameResource)
            }

            when (paintBrushUiState.paintBrushSelected?.styleTools) {
                null -> toast("Error update paint brush")
                StyleTools.FILL -> {
                    llSize.invisible()
                    rvColor.setDisabledState(true)
                    drawView.apply {
                        setColorFillMode(true)
                        setEraseMode(false)
                        setFillColor(colorPalettesUiState.colorSelected.toColorInt(), 255)
                    }
                }

                StyleTools.ERASER -> {
                    llSize.visible()
                    rvColor.setDisabledState(false)
                    drawView.apply {
                        erase()
                        setColorFillMode(false)
                        setUpDrawing(paintBrushUiState.paintBrushSelected.styleTools)
                        setEraserWidth(paintBrushUiState.paintBrushSelected.size.toFloat())
                    }
                }

                else -> {
                    Timber.d("paintBrushSelected: ${paintBrushUiState.paintBrushSelected.styleTools}")
                    llSize.visible()
                    rvColor.setDisabledState(true)
                    drawView.apply {
                        setColorFillMode(false)
                        setEraseMode(false)
                        setColor(colorPalettesUiState.colorSelected.toColorInt(), 255)
                        setUpDrawing(paintBrushUiState.paintBrushSelected.styleTools)
                        setStrokeWidth(paintBrushUiState.paintBrushSelected.size.toFloat() / 2)
                    }
                }
            }
        }
    }

    private fun initTemplateBitmap() {
        try {
            val vto: ViewTreeObserver = binding.drawView.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = binding.drawView.measuredWidth
                    height = binding.drawView.measuredHeight

                    val state = coloringViewModel.currentState

                    if (args.urlTemplate.isNotBlank()) {
                        val fileName = fileUtils.getFileNameFromUrl(args.urlTemplate)

                        originalBitmap = if (args.urlTemplate.contains(COLORING_DATA)) {
                            fileUtils.getBitmapFromPath(args.urlTemplate) //edit
                        } else {
                            fileUtils.getBitmapFromDataFolder(fileName) //new
                        }
                    }

                    binding.ivClear.tap {
                        requireContext().logEvent(EventName.edit_paint_clear_click)
                        ClearDrawingDialog {
                            binding.drawView.clearCanvas()
                            disableAllView()
                        }.show(childFragmentManager, "ClearDrawingDialog3")
                    }

                    if (state.isCanvasInitialized) {
                        state.cachedBitmap?.let { currentBitmap ->
                            binding.drawView.restoreState(
                                currentBitmap,
                                state.undoStack,
                                state.redoStack
                            )
                        }
                        binding.ivClear.tap {
                            requireContext().logEvent(EventName.edit_paint_clear_click)
                            ClearDrawingDialog {
                                binding.drawView.clearCanvas()
                                originalBitmap?.let { bmp ->
                                    val bitmapResized = bmp.resize(width, height)
                                    binding.drawView.setBitmap(bitmapResized)
                                    disableAllView()
                                }
                            }.show(childFragmentManager, "ClearDrawingDialog2")
                        }
                    } else {
                        originalBitmap?.let { bmp ->
                            val bitmapResized = bmp.resize(width, height)
                            binding.drawView.setBitmap(bitmapResized)
                        }
                    }

                    coloringViewModel.processIntent(
                        ColoringPictureUiIntent.MarkCanvasInitialized
                    )
                }
            })
        } catch (e: Exception) {
            toast("Error setBitmap $e")
        }
    }

    private fun initDrawView() {
        binding.apply {
            val vto: ViewTreeObserver = drawView.viewTreeObserver
            vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    binding.drawView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    width = drawView.measuredWidth
                    height = drawView.measuredHeight
                    drawView.init(height, width)
                }
            })
            val white = ContextCompat.getColor(requireContext(), R.color.white)
            val black = ContextCompat.getColor(requireContext(), R.color.black)
            drawView.setBackgroundColor(white)
            drawView.setColor(black, 255)
            drawView.setFillColor(black, 255)
            drawView.settingBrushStyle(StyleTools.PENCIL)
            drawView.setLimitSize(2)
        }
    }

    private fun initPaintBrush() {
        binding.seekBar.onSizeChanged = { progress ->
            var value = progress.toFloat() / 2
            if (progress < 2) {
                value = 2f
            }
            paintBrushViewModel.changeSizeBrush(progress)
            if (paintBrushViewModel.currentPaintBrush?.styleTools != StyleTools.ERASER) {
                binding.drawView.setStrokeWidth(value)
            } else {
                binding.drawView.setEraserWidth(value)
            }
        }
    }

    private fun initLogicUndoRedo() {
        disableAllView()
        binding.apply {
            drawView.setUndoRedoListener { canUndo, canRedo ->
                ivRedo.setDisabledState(canRedo)
                ivUndo.setDisabledState(canUndo)
                ivClear.setDisabledState(
                    (canUndo || canRedo),
                    R.drawable.ic_clean,
                    R.drawable.ic_clean_enable
                )
            }

            ivUndo.setOnClickListener {
                requireContext().logEvent(EventName.edit_paint_undo_click)
                drawView.undo()
            }

            ivRedo.setOnClickListener {
                requireContext().logEvent(EventName.edit_paint_redo_click)
                drawView.redo()
            }
        }
    }

    private fun disableAllView() {
        binding.apply {
            ivRedo.setDisabledState(false)
            ivUndo.setDisabledState(false)
            ivClear.setDisabledState(false, R.drawable.ic_clean, R.drawable.ic_clear_enable)
        }
    }

    override fun onDestroyView() {
        coloringViewModel.processIntent(
            ColoringPictureUiIntent.CacheCanvasState(
                binding.drawView.bitmap,
                binding.drawView.undoStack,
                binding.drawView.redoStack
            )
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        originalBitmap = null
    }
}