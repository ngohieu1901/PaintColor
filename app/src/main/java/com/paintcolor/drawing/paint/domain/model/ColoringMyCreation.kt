package com.paintcolor.drawing.paint.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ColoringMyCreation (
    val imagePath: String,
    val lastModified: Long,
    val isViewedRewardShare: Boolean = false,
    val isViewedRewardDownload: Boolean = false
): Parcelable