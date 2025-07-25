package com.himag09.taskmaster.data

import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio que permite insert, update, delete y obtener [Task] de
 * una fuente de datos determinada.
 * Asi como insertar, obtener y actualizar [User].
 */
interface TasksRepository {
    // Metodos del usuario

    /**
     * Obtiene el usuario registrado.
     */
    fun getFirstUserStream(): Flow<User?>

    /**
     * Inserta un usuario y devuelve el id del usuario insertado
     */
    suspend fun insertUser(user: User): Long

    /**
     * Actualiza un usuario.
     */
    suspend fun updateUser(user: User)

    // metodos de las tareas

    /**
     * Obtiene todas las tareas del usuario.
     */
    fun getAllTasksStream(userId: Int): Flow<List<Task>>

    /**
     * Obtiene todas las tareas del usuario pendiente.
     */
    fun getPendingTasksStream(userId: Int): Flow<List<Task>>

    /**
     * Obtiene una tarea determinada por id.
     */
    fun getTaskStream(id: Int): Flow<Task?>

    /**
     * Inserta tarea en la fuente de datos
     */
    suspend fun insertTask(task: Task)

    /**
     * Elimina una tarea en la fuente de datos.
     */
    suspend fun deleteTask(task: Task)

    /**
     * Actualiza una tarea la fuente de datos.
     */
    suspend fun updateTask(task: Task)

    /**
     * Elimina todas las tareas.
     */
    suspend fun deleteAllTasks()

    /**
     * Elimina el usuario.
     */
    suspend fun deleteUser(user: User)
}