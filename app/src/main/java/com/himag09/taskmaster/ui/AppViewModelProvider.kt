package com.himag09.taskmaster.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.himag09.taskmaster.TaskMasterApplication
import com.himag09.taskmaster.ui.detail.TaskDetailViewModel
import com.himag09.taskmaster.ui.entry.TaskEntryViewModel
import com.himag09.taskmaster.ui.home.TaskListViewModel
import com.himag09.taskmaster.ui.login.LoginViewModel
import com.himag09.taskmaster.ui.profile.ProfileViewModel
import com.himag09.taskmaster.ui.search.SearchViewModel

/**
 * Objeto que provee una Factory para crear instancias de ViewModel para
 * toda la aplicación TaskMaster.
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer para LoginViewModel
        initializer {
            LoginViewModel(
                taskMasterApplication().container.tasksRepository
            )
        }
        // Initializer para TaskListViewModel
        initializer {
            TaskListViewModel(
                // Creamos el SavedStateHandle
                this.createSavedStateHandle(),
                taskMasterApplication().container.tasksRepository
            )
        }
        // Initializer para TaskEntryViewModel
        initializer {
            TaskEntryViewModel(
                this.createSavedStateHandle(),
                taskMasterApplication().container.tasksRepository
            )
        }
        // Initializer para TaskDetailViewModel
        initializer {
            TaskDetailViewModel(
                this.createSavedStateHandle(),
                taskMasterApplication().container.tasksRepository
            )
        }
        // Initializer para ProfileViewModel
        initializer {
            ProfileViewModel(
                this.createSavedStateHandle(),
                taskMasterApplication().container.tasksRepository
            )
        }
        // Initializer para SearchViewModel
        initializer {
            SearchViewModel(
                this.createSavedStateHandle(),
                taskMasterApplication().container.tasksRepository
            )
        }
    }
}

/**
 * Funcion de extension para consultas del objeto [Application] y retorna una instancia
 * de [TaskMasterApplication].
 * Obtiene la instancia desde CreationExtras, forma segura para acceder al contenedor
 * de dependencias
 */
fun CreationExtras.taskMasterApplication(): TaskMasterApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TaskMasterApplication)