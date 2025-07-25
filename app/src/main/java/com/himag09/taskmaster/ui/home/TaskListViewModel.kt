package com.himag09.taskmaster.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.himag09.taskmaster.data.Task
import com.himag09.taskmaster.data.TasksRepository
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.ui.navigation.HomeDestination
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date

/**
 * UiState para la pantalla de la lista de tareas.
 */
data class TaskListUiState(
    val taskList: List<Task> = listOf()
)

/**
 * ViewModel para obtener todas las tareas de la base de datos room
 */
class TaskListViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    // Con SavedStateHandle tenemos un mapa de clave-valor que tiene los argumentos
    // que se pasaron a la ruta, para obtener el userId
    private val userId: Int = checkNotNull(savedStateHandle[HomeDestination.USER_ID_ARG])

    /**
     * uiState para mantener el estado de la UI que se obtiene del repositorio y se
     * actualiza automaticamente.
     */
    val uiState: StateFlow<TaskListUiState> =
        tasksRepository.getPendingTasksStream(userId)
            .map { TaskListUiState(it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = TaskListUiState()
            )

    // funcion para marcar como completo/no completo y actualizar la fecha de ultima modificacion
    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted, lastModifiedDate = Date())
            tasksRepository.updateTask(updatedTask)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}