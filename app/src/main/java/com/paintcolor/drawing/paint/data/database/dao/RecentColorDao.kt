package com.paintcolor.drawing.paint.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.paintcolor.drawing.paint.data.database.entities.RecentColorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentColorDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentColor(recentColorEntity: RecentColorEntity)

    @Query("SELECT * FROM RecentColorEntity ORDER BY colorUsageTime DESC LIMIT 9")
    fun collectAllRecentColor(): Flow<List<RecentColorEntity>>
}