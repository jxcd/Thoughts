package com.me.app.thoughts.dto

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import com.me.app.thoughts.util.TimeUtil
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Entity(tableName = "Thought")
data class Thought(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val level: Int = 4,
    val doWhat: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
) {
    fun time(): LocalDateTime = TimeUtil.parse(timestamp)
}

@Dao
interface ThoughtDao {
    @Insert
    suspend fun insert(item: Thought)

    @Update
    suspend fun update(item: Thought)

    @Query("DELETE FROM Thought where id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * from Thought ORDER BY timestamp DESC")
    fun flow(): Flow<List<Thought>>

}
