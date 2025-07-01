package com.paintcolor.drawing.paint.domain.model

import java.util.UUID

data class TemplateCategory(
    val idCategory: String = UUID.randomUUID().toString(),
    val nameCategory: String,
    val listTemplate: List<Template>
)