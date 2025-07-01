package com.paintcolor.drawing.paint.domain.model

import com.paintcolor.drawing.paint.data.database.entities.RecentColorEntity

data class RecentColor(
    val colorCode: String,
    val colorUsageTime: Long = System.currentTimeMillis()
) {
    fun toRecentColorEntity() : RecentColorEntity {
        return RecentColorEntity(colorCode = colorCode, colorUsageTime = colorUsageTime)
    }
}