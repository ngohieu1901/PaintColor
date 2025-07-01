package com.paintcolor.drawing.paint.presentations.feature.settings

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.transition.MaterialFade
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseFragment
import com.paintcolor.drawing.paint.constants.Constants.PRIVACY_POLICY
import com.paintcolor.drawing.paint.databinding.FragmentSettingsBinding
import com.paintcolor.drawing.paint.firebase.ads.AdsHelper
import com.paintcolor.drawing.paint.firebase.event.EventName
import com.paintcolor.drawing.paint.presentations.components.dialogs.RatingDialogFragment
import com.paintcolor.drawing.paint.presentations.feature.main.MainFragmentDirections
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.gone
import com.paintcolor.drawing.paint.widget.logEvent
import com.paintcolor.drawing.paint.widget.tap
import com.paintcolor.drawing.paint.widget.visible
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SettingsFragment: BaseFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {
    private val sharePref: SharePrefUtils by inject()

    override fun initData() {
        enterTransition = MaterialFade().apply {
            duration = 300L
        }
        returnTransition = MaterialFade().apply {
            duration = 300L
        }
    }

    override fun setupView() {
        requireContext().logEvent(EventName.setting_view)

        binding.apply {
            llLanguage.tap {
                requireContext().logEvent(EventName.setting_lang_click)
                safeNavigateParentNav(MainFragmentDirections.actionMainFragmentToLanguageFragment())
            }
            llShare.tap {
                requireContext().logEvent(EventName.setting_share_click)
                AdsHelper.disableResume(requireActivity())
                val intentShare = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name))
                    putExtra(Intent.EXTRA_TEXT, """     ${getString(R.string.app_name)}  https://play.google.com/store/apps/details?id=${requireContext().packageName}  """.trimIndent())
                }
                startActivity(Intent.createChooser(intentShare, "Share"))
            }
            llRate.tap {
                if (!sharePref.isRated) {
                    val rateDialog = RatingDialogFragment(false){
                        llRate.gone()
                    }
                    rateDialog.show(childFragmentManager, "RatingDialog")
                }
            }
            llPolicy.tap {
                requireContext().logEvent(EventName.setting_policy_click)
                AdsHelper.disableResume(requireActivity())
                val browserIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(PRIVACY_POLICY)
                )
                startActivity(browserIntent)
            }
        }
    }

    override fun dataCollect() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED){
                binding.apply {
                    if (sharePref.isRated) {
                        llRate.gone()
                    } else {
                        llRate.visible()
                    }
                }
            }
        }
    }
}