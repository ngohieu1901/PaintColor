package com.paintcolor.drawing.paint.data.repositories

import com.paintcolor.drawing.paint.data.local.RecentColorLocalDataSource
import com.paintcolor.drawing.paint.domain.model.RecentColor
import com.paintcolor.drawing.paint.domain.repository.RecentColorRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RecentColorRepositoryImpl(
    private val recentColorLocalDataSource: RecentColorLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
): RecentColorRepository {
    override suspend fun insertRecentColor(recentColor: RecentColor) = withContext(ioDispatcher) {
        recentColorLocalDataSource.insertRecentColor(recentColorEntity = recentColor.toRecentColorEntity())
    }

    override suspend fun collectAllRecentColor(): Flow<List<RecentColor>> =
        recentColorLocalDataSource.collectAllRecentColor().map {
            it.map { recentColorEntity ->
                recentColorEntity.toRecentColor()
            }
        }
}