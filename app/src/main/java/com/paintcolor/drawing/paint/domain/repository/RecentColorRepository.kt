package com.paintcolor.drawing.paint.domain.repository

import com.paintcolor.drawing.paint.domain.model.RecentColor
import kotlinx.coroutines.flow.Flow

interface RecentColorRepository {
    suspend fun insertRecentColor(recentColor: RecentColor)
    suspend fun collectAllRecentColor(): Flow<List<RecentColor>>
}