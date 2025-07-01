package com.paintcolor.drawing.paint.presentations.feature.view_draw

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.navigation.fragment.navArgs
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.databinding.FragmentViewVideoDrawingBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.presentations.feature.container.FilesViewModel
import com.paintcolor.drawing.paint.presentations.feature.container.ShareViewModel
import com.paintcolor.drawing.paint.presentations.feature.my_creation.MyCreationViewModel
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.toast
import com.warkiz.widget.IndicatorSeekBar
import com.warkiz.widget.OnSeekChangeListener
import com.warkiz.widget.SeekParams
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.concurrent.TimeUnit

class ViewVideoDrawingFragment :
    BaseFragment<FragmentViewVideoDrawingBinding>(FragmentViewVideoDrawingBinding::inflate) {
    private val args: ViewVideoDrawingFragmentArgs by navArgs()
    private val myCreationViewModel: MyCreationViewModel by activityViewModel()
    private val fileViewModel: FilesViewModel by activityViewModel()
    private val shareViewModel: ShareViewModel by viewModel()

    private lateinit var exoPlayer: ExoPlayer

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            if (::exoPlayer.isInitialized && exoPlayer.isPlaying) {
                val currMs = exoPlayer.currentPosition
                val durMs = exoPlayer.duration.takeIf { it > 0 } ?: return
                val percent = ((currMs * 100) / durMs).toFloat()
                binding.tvCurrentTime.text = formatTime(currMs)
                binding.isbProgress.setProgress(percent)
                handler.postDelayed(this, 100)
            }
        }
    }

    override fun initData() {

        exoPlayer = ExoPlayer.Builder(requireContext()).build()

        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(args.sketchMyCreation.filePath)))
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                when(state) {
                    Player.STATE_READY -> {
                        binding.tvTotalTime.text = formatTime(exoPlayer.duration)
                        handler.post(updateRunnable)
                        exoPlayer.play()
                        binding.ivPlayOrPause.setImageResource(R.drawable.ic_pause_video)
                    }

                    Player.STATE_ENDED -> {
                        handler.removeCallbacks(updateRunnable)
                        binding.ivPlayOrPause.setImageResource(R.drawable.ic_play_video)
                        binding.tvCurrentTime.text = formatTime(0)
                        binding.isbProgress.setProgress(0f)
                        exoPlayer.seekTo(0)
                    }

                    else -> {}
                }
            }
        })
    }

    override fun setupView() {

        binding.apply {
            playerView.player = exoPlayer
            tvName.text = File(args.sketchMyCreation.filePath).name

            ivBack.tap {
                popBackStack()
            }

            ivShare.tap {
                shareViewModel.shareImage(args.sketchMyCreation.filePath)
                myCreationViewModel.updateStateItemSketch(args.sketchMyCreation.copy(isViewedRewardShare = true))
            }

            ivPlayOrPause.tap {
                if (exoPlayer.isPlaying) {
                    exoPlayer.pause()
                    ivPlayOrPause.setImageResource(R.drawable.ic_play_video)
                    handler.removeCallbacks(updateRunnable)
                } else {
                    exoPlayer.play()
                    ivPlayOrPause.setImageResource(R.drawable.ic_pause_video)
                    handler.post(updateRunnable)
                }
            }

            ivForward.tap {
                val newPos = (exoPlayer.currentPosition + 10_000).coerceAtMost(exoPlayer.duration)
                exoPlayer.seekTo(newPos)
            }

            ivBackward.tap {
                val newPos = (exoPlayer.currentPosition - 10_000).coerceAtLeast(0)
                exoPlayer.seekTo(newPos)
            }

            var isMuted = false
            ivVolume.tap {
                isMuted = !isMuted
                exoPlayer.volume = if (isMuted) 0f else 1f
                ivVolume.setImageResource(
                    if (isMuted) R.drawable.ic_volume_off
                    else R.drawable.ic_volume_on
                )
            }

            isbProgress.onSeekChangeListener = object : OnSeekChangeListener {
                override fun onSeeking(seekParams: SeekParams) {
                    val tempPos = seekParams.progress * exoPlayer.duration / 100
                    binding.tvCurrentTime.text = formatTime(tempPos)
                }

                override fun onStartTrackingTouch(seekBar: IndicatorSeekBar) {
                    handler.removeCallbacks(updateRunnable)
                }

                override fun onStopTrackingTouch(seekBar: IndicatorSeekBar) {
                    val pct = seekBar.progress
                    val newPos = pct * exoPlayer.duration / 100
                    exoPlayer.seekTo(newPos)
                    binding.tvCurrentTime.text = formatTime(newPos)
                    handler.post(updateRunnable)
                }
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

    @SuppressLint("DefaultLocale")
    private fun formatTime(ms: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onStart() {
        super.onStart()
        binding.ivPlayOrPause.setImageResource(R.drawable.ic_play_video)
    }

    override fun onStop() {
        super.onStop()
        if (exoPlayer.isPlaying) exoPlayer.pause()
        handler.removeCallbacks(updateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        exoPlayer.stop()
        exoPlayer.release()
    }
}