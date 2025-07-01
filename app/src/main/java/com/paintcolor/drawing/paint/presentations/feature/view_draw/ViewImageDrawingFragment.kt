package com.paintcolor.drawing.paint.presentations.feature.view_draw

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentViewImageDrawingBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationViewModel
import com.paintcolor.drawing.paint.widget.loadImage
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ViewImageDrawingFragment: BaseFragment<FragmentViewImageDrawingBinding>(FragmentViewImageDrawingBinding::inflate) {
    private val args: ViewImageDrawingFragmentArgs by navArgs()
    private val fileViewModel: FilesViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()
    private val myCreationViewModel: MyCreationViewModel by activityViewModel()

    override fun initData() {

    }

    override fun setupView() {

        binding.apply {
            tvName.text = File(args.sketchMyCreation.filePath).name
            loadImage(ivImage, args.sketchMyCreation.filePath)
            ivBack.tap {
                popBackStack()
            }
            ivShare.tap {
                shareViewModel.shareImage(args.sketchMyCreation.filePath)
                myCreationViewModel.updateStateItemSketch(args.sketchMyCreation.copy(isViewedRewardShare = true))
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                shareViewModel.shareIntentResult.distinctUntilChanged().collectLatest { intent ->
                    if (intent == null) {
                        toast(getString(R.string.failed_to_share_image))
                    } else {
                        AdsHelper.disableResume(requireActivity())
                        startActivity(intent)
                    }
                }
            }
        }
    }

}