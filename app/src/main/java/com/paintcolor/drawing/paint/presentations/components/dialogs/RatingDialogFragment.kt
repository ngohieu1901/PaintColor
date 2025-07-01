package com.paintcolor.drawing.paint.presentations.components.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.paintcolor.drawing.paint.R
import com.paintcolor.drawing.paint.base.BaseDialogFragment
import com.paintcolor.drawing.paint.databinding.DialogRatingBinding
import com.paintcolor.drawing.paint.firebase.event.AdmobEvent
import com.paintcolor.drawing.paint.utils.SharePrefUtils
import com.paintcolor.drawing.paint.widget.tap

class RatingDialogFragment(private val isFinishActivity: Boolean, private val onDismissCallback: () -> Unit) : BaseDialogFragment<DialogRatingBinding>(DialogRatingBinding::inflate) {

    override fun setupView() {
        AdmobEvent.logEvent(context, "rate_show", Bundle())
        binding.tvContent.text = getString(
            R.string.we_d_greatly_appreciate_if_you_can_rate_us,
            getString(R.string.app_name)
        )
        changeRating()
        binding.rtb.rating = 5f
        binding.apply {
            btnRate.tap {
                if (rtb.rating == 0f) {
                    Toast.makeText(activity, R.string.please_give_rating, Toast.LENGTH_SHORT)
                        .show()
                    return@tap
                }
                AdmobEvent.logEvent(context, "rate_submit", Bundle())
                dismiss()
                if (rtb.rating < 4) {
                    sendMail()
                } else {
                    sendRate()
                }
                SharePrefUtils(requireActivity()).isRated = true
            }
            btnLater.tap {
                AdmobEvent.logEvent(context, "rate_not_now", Bundle())
                dismiss()
                checkFinishApplication()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissCallback()
    }

    override fun initData() {

    }

    override fun dataCollect() {

    }

    private fun checkFinishApplication() {
        if (isFinishActivity) {
            activity?.let { activity ->
                SharePrefUtils(activity).countExitApp++
                activity.finishAffinity()
            }
        }
    }

    private fun sendRate() {
        activity?.let { activity ->
            val manager: ReviewManager = ReviewManagerFactory.create(activity)
            val request: Task<ReviewInfo> = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val reviewInfo: ReviewInfo = task.result
                    val flow: Task<Void> =
                        manager.launchReviewFlow(activity, reviewInfo)
                    flow.addOnSuccessListener {
                        checkFinishApplication()
                    }
                } else {
                    dismiss()
                    checkFinishApplication()
                }
            }
        }
    }

    private fun sendMail() {
        activity?.let { activity ->
            Toast.makeText(
                activity,
                R.string.thank_u,
                Toast.LENGTH_SHORT
            ).show()
            checkFinishApplication()
        }
    }

    private fun changeRating() {
        binding.rtb.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                context?.let { context ->
                    when (rating) {
                        1f -> {
                            binding.btnRate.text = context.getText(R.string.thank_u)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_1)
                            binding.btnLater.visibility = View.VISIBLE
                        }

                        2f -> {
                            binding.btnRate.text = context.getText(R.string.thank_u)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_2)
                            binding.btnLater.visibility = View.VISIBLE
                        }

                        3f -> {
                            binding.btnRate.text = context.getText(R.string.thank_u)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_3)
                            binding.btnLater.visibility = View.VISIBLE
                        }

                        4f -> {
                            binding.btnRate.text = context.getText(R.string.thank_u)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_4)
                            binding.btnLater.visibility = View.GONE
                        }

                        5f -> {
                            binding.btnRate.text = context.getText(R.string.thank_u)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_5)
                            binding.btnLater.visibility = View.GONE
                        }

                        else -> {
                            binding.btnRate.text = context.getText(R.string.rate)
                            binding.imgIcon.setImageResource(R.drawable.ic_star_0)
                            binding.btnLater.visibility = View.VISIBLE
                        }
                    }
                }
            }
    }
}