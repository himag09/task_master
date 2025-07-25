package com.himag09.taskmaster.data

import android.content.Context

/**
 * Contenedor de dependencias para la aplicacion.
 */
interface AppContainer {
    val tasksRepository: TasksRepository
}

/**
 * Implementacion de [AppContainer] (contenedor de dependencias) que
 * crea y provee las instancias.
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementacion para [TasksRepository].
     */
    override val tasksRepository: TasksRepository by lazy {
        OfflineTasksRepository(
            TaskMasterDatabase.getDatabase(context).userDao(),
            TaskMasterDatabase.getDatabase(context).taskDao()
        )
    }
}