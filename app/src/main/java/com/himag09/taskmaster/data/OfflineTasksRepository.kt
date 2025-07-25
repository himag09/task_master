package com.himag09.taskmaster.data

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Clase que implementa la interfaza y se comunica con los DAOs
 */
class OfflineTasksRepository(private val userDao: UserDao, private val taskDao: TaskDao) :
    TasksRepository {
    // Metodos del usuario.
    override fun getFirstUserStream(): Flow<User?> = userDao.getFirstUser()
    override suspend fun insertUser(user: User): Long = userDao.insert(user)
    override suspend fun updateUser(user: User) = userDao.update(user)

    // Metodos de las tareas.
    override fun getAllTasksStream(userId: Int): Flow<List<Task>> =
        taskDao.getAllTasksForUser(userId)

    override fun getTaskStream(id: Int): Flow<Task?> {
        return try {
            taskDao.getTask(id)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al obtener tarea", e)
            flowOf(null)
        }
    }

    override fun getPendingTasksStream(userId: Int): Flow<List<Task>> {
        return try {
            taskDao.getPendingTasks(userId)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al obtener tareas pendientes", e)
            flowOf(emptyList())
        }
    }

    override suspend fun insertTask(task: Task) {
        try {
            taskDao.insert(task)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al insertar la tarea", e)
        }
    }

    override suspend fun deleteTask(task: Task) {
        try {
            taskDao.delete(task)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al eliminar la tarea", e)
        }
    }

    override suspend fun updateTask(task: Task) {
        try {
            taskDao.update(task)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al actualizar la tarea", e)
        }
    }

    override suspend fun deleteAllTasks() {
        try {
            taskDao.deleteAllTasks()
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al eliminar todas las tareas", e)
        }
    }

    override suspend fun deleteUser(user: User) {
        try {
            userDao.deleteUser(user)
        } catch (e: Exception) {
            Log.e("TasksRepository", "Error al eliminar el usuario", e)
        }
    }
}