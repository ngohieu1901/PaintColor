package com.paintcolor.drawing.paint.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SketchMyCreation(
    val filePath: String,
    val lastModified: Long,
    val duration: String,
    val isVideo: Boolean,
    val isViewedRewardShare: Boolean = false,
    val isViewedRewardDownload: Boolean = false
): Parcelable