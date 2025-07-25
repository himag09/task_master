package com.himag09.taskmaster.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Query
import androidx.room.OnConflictStrategy
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * from tasks WHERE id = :id")
    fun getTask(id: Int): Flow<Task>

    @Query("SELECT * from tasks WHERE userId = :userId ORDER BY dueDate ASC")
    fun getAllTasksForUser(userId: Int): Flow<List<Task>>

    // obtenemos tareas que no estan completadas, las ordenamos por fecha de entrega.
    @Query("SELECT * from tasks WHERE userId = :userId AND isCompleted = 0 ORDER BY dueDate ASC")
    fun getPendingTasks(userId: Int): Flow<List<Task>>

    // para borrar todas las tareas.
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}