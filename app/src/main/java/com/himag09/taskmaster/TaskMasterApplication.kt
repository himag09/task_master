package com.himag09.taskmaster

import android.app.Application
import com.himag09.taskmaster.data.AppContainer
import com.himag09.taskmaster.data.AppDataContainer

/**
 * Para que [AppContainer] esté disponible en toda la aplicación, lo inicializamos en
 * esta clase Application
 */
class TaskMasterApplication : Application() {

    /**
     * Instancia de [AppContainer] utilizada por el resto de clases para obtener dependencias
     *
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}