package com.paintcolor.drawing.paint.domain.repository

import com.paintcolor.drawing.paint.data.result.DataResult
import com.paintcolor.drawing.paint.domain.model.Template
import com.paintcolor.drawing.paint.domain.model.TemplateCategory

interface TemplateRepository {
    suspend fun getAllTemplateOnServer(): DataResult<List<Template>>
    suspend fun downloadTemplate(imageUrl: String): Long
}