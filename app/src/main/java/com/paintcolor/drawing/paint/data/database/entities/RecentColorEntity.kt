package com.paintcolor.drawing.paint.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.paintcolor.drawing.paint.domain.model.RecentColor

@Entity
data class RecentColorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val colorCode: String,
    val colorUsageTime: Long
) {
    fun toRecentColor() : RecentColor {
        return RecentColor(colorCode = colorCode, colorUsageTime = colorUsageTime)
    }
}