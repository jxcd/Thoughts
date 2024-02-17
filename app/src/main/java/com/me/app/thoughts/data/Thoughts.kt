package com.me.app.thoughts.data

import androidx.room.ColumnInfo
import androidx.room.Dao
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
    @ColumnInfo(defaultValue = "0", index = true)
    val pid: Int = 0,

    val level: Int = 4,
    val doWhat: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),

    @ColumnInfo(defaultValue = "0")
    val priority: Int = 0,
    @ColumnInfo(defaultValue = "true")
    val visible: Boolean = true,
) {
    fun time(): LocalDateTime = TimeUtil.parse(timestamp)
}

const val ORDER_BY = "priority DESC, timestamp DESC, id DESC"

@Dao
interface ThoughtDao {
    @Insert
    suspend fun insert(item: Thought)

    @Update
    suspend fun update(item: Thought)

    @Query("DELETE FROM Thought where id = :id")
    suspend fun delete(id: Int)

    @Query("SELECT * from Thought where pid = :pid and timestamp >= :timestamp ORDER BY $ORDER_BY")
    suspend  fun listByPidAndTimestamp(pid: Int = 0, timestamp: Long = TimeUtil.timestamp(0, 0)): List<Thought>

    @Query("SELECT * from Thought where pid = :pid ORDER BY $ORDER_BY")
    fun flowByPid(pid: Int): Flow<List<Thought>>

    @Query("SELECT * from Thought where pid = :pid and visible = :visible ORDER BY $ORDER_BY")
    fun flowByPidVisible(pid: Int, visible: Boolean = true): Flow<List<Thought>>

    fun flow(pid: Int = 0, onlyVisible: Boolean = true): Flow<List<Thought>> =
        if (onlyVisible) flowByPidVisible(pid, true) else flowByPid(pid)

}
