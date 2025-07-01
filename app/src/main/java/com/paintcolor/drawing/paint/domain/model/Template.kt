package com.paintcolor.drawing.paint.domain.model

data class Template(
    val id: Int,
    val imagePath: String,
    val category: String,
    val stateDownload: Int,
    val isViewedReward: Boolean = false,
) {
    companion object {
        const val DOWNLOADING = 0
        const val DOWNLOADED = 1
        const val WAIT_FOR_DOWNLOAD = 2
    }
}