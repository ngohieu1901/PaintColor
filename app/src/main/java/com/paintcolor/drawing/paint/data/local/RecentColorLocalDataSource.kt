package com.paintcolor.drawing.paint.data.local

import com.paintcolor.drawing.paint.data.database.dao.RecentColorDao
import com.paintcolor.drawing.paint.data.database.entities.RecentColorEntity

class RecentColorLocalDataSource(private val recentColorDao: RecentColorDao) {
    suspend fun insertRecentColor(recentColorEntity: RecentColorEntity) {
        recentColorDao.insertRecentColor(recentColorEntity = recentColorEntity)
    }

    fun collectAllRecentColor() = recentColorDao.collectAllRecentColor()
}