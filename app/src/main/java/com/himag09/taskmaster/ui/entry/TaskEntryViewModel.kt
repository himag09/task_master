package com.himag09.taskmaster.ui.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himag09.taskmaster.data.Priority
import com.himag09.taskmaster.data.Task
import com.himag09.taskmaster.data.TasksRepository
import com.himag09.taskmaster.ui.navigation.TaskDetailDestination
import com.himag09.taskmaster.ui.navigation.TaskEntryDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

/**
 * UiState para la pantalla de creación/edicion de tareas.
 */
data class TaskUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: Date = Date(),
    val priority: Priority = Priority.MEDIUM,
    val isCompleted: Boolean = false,
    val creationDate: Date? = null,
    val lastModifiedDate: Date? = null,
    val isEntryValid: Boolean = false
)

/**
 * Representa los detalles de una tarea para la UI.
 * Con esto facilitamos la conversion entre la entidad Task y el TaskUiState.
 */
data class TaskDetails(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String = "",
    val description: String = "",
    val dueDate: Date = Date(),
    val priority: Priority = Priority.MEDIUM,
    val creationDate: Date? = null,
    val lastModifiedDate: Date? = null,
    val isCompleted: Boolean = false
)

class TaskEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    // Obtenemos userId del argumento de navegación
    private var userId: Int? = savedStateHandle[TaskEntryDestination.USER_ID_ARG]

    // Obtenemos taskId del argumento de navegación
    private val taskId: Int? = savedStateHandle[TaskDetailDestination.TASK_ID_ARG]

    init {
        // Si hay un taskId cargamos los datos.
        if (taskId != null) {
            viewModelScope.launch {
                tasksRepository.getTaskStream(taskId)
                    .filterNotNull()
                    .first()
                    .let { task ->
                        userId = task.userId
                        _uiState.update {
                            TaskUiState(
                                id = task.id,
                                title = task.title,
                                description = task.description ?: "",
                                dueDate = task.dueDate,
                                priority = task.priority,
                                isCompleted = task.isCompleted,
                                creationDate = task.creationDate,
                                lastModifiedDate = task.lastModifiedDate,
                                isEntryValid = true
                            )
                        }
                    }
            }
        }
    }

    fun updateUiState(newUiState: TaskUiState) {
        _uiState.update {
            newUiState.copy(isEntryValid = validateInput(newUiState))
        }
    }

    suspend fun saveTask() {
        if (validateInput()) {
            val current = _uiState.value
            val task = Task(
                id = current.id,
                userId = userId ?: throw IllegalStateException("User ID no disponible"),
                title = current.title,
                description = current.description,
                dueDate = current.dueDate,
                priority = current.priority,
                isCompleted = current.isCompleted,
                creationDate = current.creationDate ?: Date(),
                lastModifiedDate = Date()
            )
            // Si el ID del estado es 0, es una nueva tarea. Si no, es una actualización.
            if (current.id == 0) {
                tasksRepository.insertTask(task)
            } else {
                tasksRepository.updateTask(task)
            }
        }
    }

    private fun validateInput(uiState: TaskUiState = _uiState.value): Boolean {
        return with(uiState) {
            title.isNotBlank() && title.length > 3
        }
    }
}

/**
 * Funcion de extension para convertir [Task] a [TaskDetails]
 */
fun Task.toTaskDetails(): TaskDetails = TaskDetails(
    id = id,
    userId = userId,
    title = title,
    description = description ?: "",
    dueDate = dueDate,
    priority = priority,
    isCompleted = isCompleted,
    creationDate = creationDate,
    lastModifiedDate = lastModifiedDate
)

/**
 * Funcion de extension para convertir [TaskDetails] a [Task]
 */
fun TaskDetails.toTask(): Task = Task(
    id = id,
    userId = userId,
    title = title,
    description = description,
    dueDate = dueDate,
    priority = priority,
    isCompleted = isCompleted,
    creationDate = Date(), // Se genera al crear la tarea
    lastModifiedDate = Date() // se genera al actualizar una tarea
)
