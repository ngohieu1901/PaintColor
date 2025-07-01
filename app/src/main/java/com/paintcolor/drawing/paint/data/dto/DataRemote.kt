package com.paintcolor.drawing.paint.data.dto

import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.Template.Companion.WAIT_FOR_DOWNLOAD

data class TemplateDto(
    val id: Int,
    val imagePath: String,
    val category: String,
) {
    fun toTemplate(): Template {
        return Template(
            id = id,
            imagePath = imagePath,
            category = category,
            stateDownload = WAIT_FOR_DOWNLOAD
        )
    }
}